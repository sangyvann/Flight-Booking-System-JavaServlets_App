# Flight Booking System - Current Architecture Diagrams

## 1. High-Level System Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        Browser[Web Browser]
    end

    subgraph "Presentation Layer"
        JSP[JSP Pages<br/>home.jsp, BookFlight.jsp<br/>ShowFlights.jsp, etc.]
        Templates[JSP Templates<br/>AdminHeader.jsp<br/>CustomerHeader.jsp<br/>ManagerPanel.jsp]
    end

    subgraph "Web Container - Apache Tomcat"
        subgraph "Security Layer"
            SecurityFilter[SecurityFilter<br/>Cache Control Headers]
            XSSFilter[XSSFilter<br/>XSS Protection]
            TomcatAuth[Tomcat Form-Based<br/>Authentication]
        end

        subgraph "Controller Layer - Servlets"
            LoginMgr[LoginManager]
            LogoutMgr[LogoutManager]
            
            subgraph "Admin Servlets"
                ChangeFeatures[ChangeFeatures]
                SetSeats[SetSeats]
            end
            
            subgraph "Manager Servlets"
                ApproveFeatures[ApproveFeatures]
                DisapproveFeatures[DisapproveFeatures]
                ApproveSeats[ApproveSeats]
                DisapproveSeats[DisapproveSeats]
            end
            
            subgraph "Customer Servlets"
                SearchFlights[SearchFlights]
                ChooseFlight[ChooseFlight]
                BookFlight[BookFlight]
                CurrentBooking[CurrentBooking]
            end
            
            OriginCompleter[OriginCompleter<br/>AJAX Autocomplete]
        end

        subgraph "Web Services"
            SOAP[PriceAndSeats<br/>SOAP Web Service]
        end

        subgraph "Business Logic Layer"
            FBS[FBS.java<br/>Flight Booking System<br/>Core Business Logic]
        end

        subgraph "Model Layer"
            Models[Domain Models<br/>Flight, Customer, Seat<br/>Features, Employee, Person]
        end

        subgraph "Application Scope"
            Context[ServletContext<br/>Application-wide Data]
        end
    end

    subgraph "Data Layer"
        MSAccess[(Microsoft Access<br/>airlines.accdb<br/>Tables: Flights, Customers<br/>Seats, Features, Cities)]
    end

    subgraph "External Client"
        WSTester[WSTester<br/>SOAP Client<br/>Testing Application]
    end

    Browser -->|HTTP/HTTPS| SecurityFilter
    SecurityFilter --> XSSFilter
    XSSFilter --> TomcatAuth
    TomcatAuth -->|Authenticated| JSP
    TomcatAuth -->|Login Required| LoginMgr
    
    JSP --> Templates
    JSP -->|Form Submit| ChangeFeatures
    JSP -->|Form Submit| SearchFlights
    JSP -->|Form Submit| BookFlight
    JSP -->|AJAX Request| OriginCompleter
    
    LoginMgr --> Context
    ChangeFeatures --> FBS
    SetSeats --> FBS
    ApproveFeatures --> FBS
    ApproveSeats --> FBS
    SearchFlights --> FBS
    BookFlight --> FBS
    OriginCompleter --> FBS
    
    FBS --> Models
    FBS -->|JDBC - UCanAccess| MSAccess
    Models --> Context
    
    WSTester -->|SOAP/XML| SOAP
    SOAP --> FBS
    FBS -->|Query| MSAccess

    style Browser fill:#e1f5ff
    style MSAccess fill:#ffe1e1
    style TomcatAuth fill:#fff4e1
    style SecurityFilter fill:#fff4e1
    style XSSFilter fill:#fff4e1
    style SOAP fill:#e1ffe1
```

## 2. Authentication & Authorization Flow

```mermaid
sequenceDiagram
    participant User
    participant Browser
    participant SecurityFilter
    participant TomcatAuth
    participant LoginManager
    participant ServletContext
    participant JSP

    User->>Browser: Access Protected Resource
    Browser->>SecurityFilter: HTTP Request
    SecurityFilter->>TomcatAuth: Check Authentication
    
    alt Not Authenticated
        TomcatAuth->>Browser: Redirect to /WEB-INF/Login.jsp
        Browser->>User: Show Login Form
        User->>Browser: Enter Credentials
        Browser->>TomcatAuth: POST j_security_check
        TomcatAuth->>TomcatAuth: Validate against tomcat-users.xml
        
        alt Invalid Credentials
            TomcatAuth->>Browser: Redirect to ErrorPage.jsp
        else Valid Credentials
            TomcatAuth->>LoginManager: Forward to /Login
            LoginManager->>LoginManager: Check User Role
            
            alt Admin Role
                LoginManager->>Browser: Redirect to ChangeFeatures.jsp
            else Manager Role
                LoginManager->>Browser: Redirect to ApproveFeatures.jsp
            else Customer Role
                LoginManager->>ServletContext: Get Customer List
                ServletContext->>LoginManager: Return Customers
                LoginManager->>LoginManager: Find Customer by Email
                LoginManager->>LoginManager: Store Customer in Session
                LoginManager->>Browser: Forward to CurrentBooking.do
            end
        end
    else Already Authenticated
        SecurityFilter->>JSP: Serve Protected Resource
        JSP->>Browser: Render Page
    end
```

## 3. Customer Booking Flow

```mermaid
sequenceDiagram
    participant Customer
    participant Browser
    participant BookFlightServlet
    participant SearchFlightsServlet
    participant ChooseFlightServlet
    participant BookFlightServlet2
    participant FBS
    participant Database
    participant EmailService

    Customer->>Browser: Navigate to Book Flight
    Browser->>BookFlightServlet: GET /BookFlight
    BookFlightServlet->>Browser: Return BookFlight.jsp
    
    Customer->>Browser: Fill Search Form<br/>(Origin, Destination, Date, Class, Passengers)
    Browser->>SearchFlightsServlet: POST /SearchFlights.do
    SearchFlightsServlet->>FBS: Get Flights from Context
    FBS->>SearchFlightsServlet: Return Flight List
    
    Note over SearchFlightsServlet: Filter by:<br/>- Cities Match<br/>- Date Match<br/>- Seats Available<br/>- Class Available<br/>- No Pending Changes
    
    SearchFlightsServlet->>Browser: Forward to ShowFlights.jsp
    Browser->>Customer: Display Available Flights
    
    Customer->>Browser: Select Flight
    Browser->>ChooseFlightServlet: POST /ChooseFlight.do
    ChooseFlightServlet->>ChooseFlightServlet: Store Selection in Session
    ChooseFlightServlet->>Browser: Forward to ShowItinery.jsp
    Browser->>Customer: Display Itinerary & Price
    
    Customer->>Browser: Confirm Booking
    Browser->>BookFlightServlet2: POST /BookFlight.do
    BookFlightServlet2->>FBS: Create Booking
    FBS->>Database: INSERT Seat Record
    FBS->>Database: UPDATE Flight (Decrement Available Seats)
    Database->>FBS: Success
    FBS->>BookFlightServlet2: Return Booking
    BookFlightServlet2->>EmailService: Send Confirmation Email
    BookFlightServlet2->>Browser: Show Confirmation Page
    Browser->>Customer: Display Booking Confirmation
```

## 4. Admin/Manager Approval Workflow

```mermaid
sequenceDiagram
    participant Admin
    participant AdminServlet
    participant FBS
    participant Database
    participant Manager
    participant ManagerServlet

    rect rgb(255, 240, 200)
        Note over Admin,Database: Admin Phase - Make Changes
        Admin->>AdminServlet: POST /ChangeFeatures.do<br/>(New Prices, Seat Features)
        AdminServlet->>FBS: Get Features from Context
        
        Note over AdminServlet: Store Current Values in:<br/>- newSeatPitch<br/>- newSeatWidth<br/>- newPrice<br/>etc.
        
        AdminServlet->>FBS: Update Features (Temporary)
        FBS->>FBS: Set isChanged = true
        AdminServlet->>Admin: Redirect to ChangeFeatures.jsp<br/>(Show Pending Changes)
    end

    rect rgb(200, 255, 200)
        Note over Manager,Database: Manager Phase - Review & Approve
        Manager->>ManagerServlet: GET /ApproveFeatures.jsp
        ManagerServlet->>FBS: Get Features with isChanged=true
        FBS->>ManagerServlet: Return Pending Changes
        ManagerServlet->>Manager: Display Changes for Review
        
        alt Manager Approves
            Manager->>ManagerServlet: POST /ApproveFeatures.do
            ManagerServlet->>FBS: Commit Changes
            FBS->>Database: UPDATE Features<br/>(Make New Values Current)
            FBS->>FBS: Set isChanged = false
            Database->>FBS: Success
            ManagerServlet->>Manager: Redirect with Success Message
        else Manager Disapproves
            Manager->>ManagerServlet: POST /DisapproveFeatures.do
            ManagerServlet->>FBS: Revert Changes
            FBS->>FBS: Restore Old Values
            FBS->>FBS: Set isChanged = false
            ManagerServlet->>Manager: Redirect with Rejection Message
        end
    end
```

## 5. Data Model & Relationships

```mermaid
erDiagram
    CUSTOMERS ||--o{ SEATS : books
    FLIGHTS ||--o{ SEATS : contains
    FEATURES ||--o{ SEATS : "has type"
    
    CUSTOMERS {
        string email PK
        string name
    }
    
    FLIGHTS {
        string FlightName PK
        string DepartureCity
        string ArrivalCity
        date DepartureDate
        date ArrivalDate
        int TotalSeats
        int CurrentSeats
        int EconomySeats
        int BusinessSeats
        int FirstSeats
        int OldESeats
        int OldBSeats
        int OldFSeats
        int OldTSeats
        boolean isChanged
    }
    
    SEATS {
        int SeatNumber PK
        string flightName FK
        string CustomerEmail FK
        string FeatureType FK
    }
    
    FEATURES {
        int type PK "0=Economy, 1=Business, 2=First"
        int price
        int newPrice
        double seatPitch
        double NewSeatPitch
        double seatWidth
        double NewSeatWidth
        string VideoType
        string NewVideoType
        string SeatType
        string NewSeatType
        string PowerType
        string NewPowerType
        string Wifi
        string NewWifi
        string SpecialFood
        string NewSpecialFood
        boolean isChanged
    }
    
    CITIES {
        string CITYNAME PK
    }
```

## 6. Application Initialization Flow

```mermaid
sequenceDiagram
    participant Tomcat
    participant SContextListener
    participant Database
    participant FBS
    participant ServletContext

    Tomcat->>SContextListener: contextInitialized()
    
    Note over SContextListener: Load Database Path<br/>airlines.accdb
    
    SContextListener->>SContextListener: Load UCanAccess JDBC Driver
    SContextListener->>Database: Establish Connection
    Database->>SContextListener: Connection Object
    
    SContextListener->>FBS: new FBS()
    
    par Load All Data
        SContextListener->>FBS: populateFeatures(connection)
        FBS->>Database: SELECT * FROM Features
        Database->>FBS: Features ResultSet
        FBS->>SContextListener: ArrayList<Features>
        
        SContextListener->>FBS: populateCustomers(connection)
        FBS->>Database: SELECT * FROM Customers
        Database->>FBS: Customers ResultSet
        FBS->>SContextListener: ArrayList<Customer>
        
        SContextListener->>FBS: getAllFlights(connection, customers, features)
        FBS->>Database: SELECT * FROM Flights
        Database->>FBS: Flights ResultSet
        
        loop For Each Flight
            FBS->>Database: SELECT * FROM Seats WHERE flightName = ?
            Database->>FBS: Seats ResultSet
            FBS->>FBS: Link Seats to Customers & Features
        end
        
        FBS->>SContextListener: ArrayList<Flight>
    end
    
    SContextListener->>ServletContext: setAttribute("con", connection)
    SContextListener->>ServletContext: setAttribute("fbs", fbs)
    SContextListener->>ServletContext: setAttribute("features", features)
    SContextListener->>ServletContext: setAttribute("customers", customers)
    SContextListener->>ServletContext: setAttribute("flights", flights)
    
    Note over ServletContext: All Data Now in Memory<br/>Available to All Servlets
    
    SContextListener->>Tomcat: Initialization Complete
```

## 7. Security Architecture

```mermaid
graph TB
    subgraph "Security Layers"
        subgraph "Layer 1: Network Security"
            HTTPS[HTTPS/TLS<br/>Encrypted Transport]
        end
        
        subgraph "Layer 2: Filter Chain"
            SecurityFilter[SecurityFilter<br/>- Cache-Control: no-cache<br/>- Pragma: no-cache<br/>- Expires: 0]
            XSSFilter[XSSFilter<br/>- XSSRequestWrapper<br/>- Sanitize Input Parameters]
        end
        
        subgraph "Layer 3: Authentication"
            FormAuth[Form-Based Authentication<br/>- Login Page: /WEB-INF/Login.jsp<br/>- Error Page: /ErrorPage.jsp<br/>- Credentials: tomcat-users.xml]
        end
        
        subgraph "Layer 4: Authorization"
            SecurityConstraints[Security Constraints<br/>web.xml]
            
            AdminResources[Admin Resources<br/>- ChangeFeatures.jsp<br/>- SetSeats.jsp<br/>- /ChangeFeatures.do<br/>- /SetSeats.do]
            
            ManagerResources[Manager Resources<br/>- ApproveFeatures.jsp<br/>- ApproveSeats.jsp<br/>- /ApproveFeatures.do<br/>- /ApproveSeats.do]
            
            CustomerResources[Customer Resources<br/>- BookFlight.jsp<br/>- CurrentBooking.jsp<br/>- /SearchFlights.do<br/>- /BookFlight.do]
        end
        
        subgraph "Layer 5: Data Security"
            PreparedStmt[Prepared Statements<br/>SQL Injection Prevention]
            ParamBinding[Parameter Binding<br/>No String Concatenation]
        end
    end
    
    HTTPS --> SecurityFilter
    SecurityFilter --> XSSFilter
    XSSFilter --> FormAuth
    FormAuth --> SecurityConstraints
    
    SecurityConstraints --> AdminResources
    SecurityConstraints --> ManagerResources
    SecurityConstraints --> CustomerResources
    
    AdminResources --> PreparedStmt
    ManagerResources --> PreparedStmt
    CustomerResources --> PreparedStmt
    
    PreparedStmt --> ParamBinding

    style HTTPS fill:#90EE90
    style SecurityFilter fill:#FFD700
    style XSSFilter fill:#FFD700
    style FormAuth fill:#87CEEB
    style PreparedStmt fill:#90EE90
```

## 8. Web Services Architecture

```mermaid
graph LR
    subgraph "SOAP Web Service"
        WSServlet[WSServlet<br/>com.sun.xml.ws.transport]
        PriceAndSeats[PriceAndSeats<br/>@WebService]
        
        subgraph "Operations"
            GetPrice[@WebMethod<br/>GetSeatPrice<br/>params: origin, destination<br/>returns: int price]
            GetSeats[@WebMethod<br/>GetAvailableSeats<br/>params: flightNumber, date<br/>returns: int seats]
        end
    end
    
    subgraph "Business Logic"
        FBSStatic[FBS Static Methods<br/>getPrice&#40;origin, destination&#41;<br/>getSeats&#40;flightNumber, date&#41;]
    end
    
    subgraph "Data Access"
        DirectDB[Direct Database Access<br/>New Connection Per Request<br/>UCanAccess JDBC]
        MSAccess[(airlines.accdb)]
    end
    
    subgraph "External Client"
        WSTester[WSTester Project<br/>JAX-WS Client<br/>Generated Stubs]
        WSDL[WSDL<br/>http://localhost:8080/PriceAndSeats?wsdl]
    end
    
    WSTester -->|SOAP Request| WSServlet
    WSServlet --> PriceAndSeats
    PriceAndSeats --> GetPrice
    PriceAndSeats --> GetSeats
    
    GetPrice --> FBSStatic
    GetSeats --> FBSStatic
    
    FBSStatic --> DirectDB
    DirectDB -->|SQL Query| MSAccess
    
    WSTester -.->|Discover| WSDL
    
    style WSServlet fill:#e1ffe1
    style PriceAndSeats fill:#e1ffe1
    style WSTester fill:#ffe1e1
```

## 9. Session & State Management

```mermaid
graph TB
    subgraph "Application Scope - ServletContext"
        Connection[Database Connection<br/>Shared Across All Users]
        FBSInstance[FBS Instance<br/>Business Logic]
        Features[Features List<br/>ArrayList&lt;Features&gt;]
        Customers[Customers List<br/>ArrayList&lt;Customer&gt;]
        Flights[Flights List<br/>ArrayList&lt;Flight&gt;<br/>with Seats]
    end
    
    subgraph "Session Scope - HttpSession"
        subgraph "Customer Session"
            CustomerObj[Customer Object<br/>Current User Info]
            SelectedFlight[Selected Flight<br/>Booking in Progress]
            SearchCriteria[Search Criteria<br/>Origin, Destination, Date]
        end
        
        subgraph "Admin Session"
            AdminUser[Admin User Info]
            PendingChanges[Pending Feature Changes]
        end
        
        subgraph "Manager Session"
            ManagerUser[Manager User Info]
            ReviewQueue[Items to Review]
        end
    end
    
    subgraph "Request Scope"
        FormData[Form Parameters]
        ValidationErrors[Validation Errors]
        SearchResults[Search Results]
    end
    
    Connection -.->|Read Only| CustomerSession
    Connection -.->|Read Only| AdminSession
    Connection -.->|Read Only| ManagerSession
    
    Features -.->|Shared Read| CustomerSession
    Features -.->|Read/Write| AdminSession
    Features -.->|Read/Approve| ManagerSession
    
    Flights -.->|Read/Search| CustomerSession
    Flights -.->|Read/Write| AdminSession
    Flights -.->|Read/Approve| ManagerSession
    
    CustomerObj --> FormData
    SelectedFlight --> FormData
    
    style Connection fill:#ffe1e1
    style Features fill:#e1f5ff
    style Customers fill:#e1f5ff
    style Flights fill:#e1f5ff
```

## 10. Deployment Architecture

```mermaid
graph TB
    subgraph "Development Environment"
        NetBeans[NetBeans IDE<br/>Project: TurkishAirlines]
        SourceCode[Source Code<br/>src/java/]
        WebContent[Web Content<br/>web/]
        Libraries[Libraries<br/>Libs/]
    end
    
    subgraph "Build Process"
        AntBuild[Apache Ant<br/>build.xml]
        Compilation[Java Compilation<br/>.java → .class]
        WARPackaging[WAR Packaging<br/>TurkishAirlines.war]
    end
    
    subgraph "Apache Tomcat Server"
        TomcatContainer[Tomcat Container]
        
        subgraph "Deployed Application"
            WebApp[TurkishAirlines<br/>Web Application]
            ClassFiles[WEB-INF/classes/<br/>Compiled Servlets]
            LibJars[WEB-INF/lib/<br/>JAR Dependencies]
            WebXML[WEB-INF/web.xml<br/>Deployment Descriptor]
            JSPFiles[JSP Files<br/>Views]
            Assets[assets/<br/>CSS, JS, Images]
        end
        
        subgraph "Tomcat Configuration"
            TomcatUsers[conf/tomcat-users.xml<br/>User Credentials & Roles]
            ContextXML[META-INF/context.xml<br/>Context Configuration]
        end
        
        subgraph "Runtime"
            JVM[Java Virtual Machine<br/>JDK 8]
            ClassLoader[Web Application<br/>ClassLoader]
        end
    end
    
    subgraph "Database"
        AccessDB[airlines.accdb<br/>WEB-INF/classes/]
        UCanAccess[UCanAccess Driver<br/>JDBC Bridge]
    end
    
    subgraph "Client Access"
        Browser1[Web Browser<br/>http://localhost:8080/TurkishAirlines/]
        WSClient[SOAP Client<br/>http://localhost:8080/TurkishAirlines/PriceAndSeats]
    end
    
    NetBeans --> AntBuild
    SourceCode --> Compilation
    WebContent --> WARPackaging
    Libraries --> WARPackaging
    Compilation --> WARPackaging
    
    WARPackaging --> TomcatContainer
    
    TomcatContainer --> WebApp
    WebApp --> ClassFiles
    WebApp --> LibJars
    WebApp --> WebXML
    WebApp --> JSPFiles
    WebApp --> Assets
    
    TomcatUsers -.->|Authentication| TomcatContainer
    ContextXML -.->|Configuration| WebApp
    
    JVM --> ClassLoader
    ClassLoader --> ClassFiles
    ClassLoader --> LibJars
    
    ClassFiles --> AccessDB
    LibJars --> UCanAccess
    UCanAccess --> AccessDB
    
    Browser1 -->|HTTP Request| TomcatContainer
    WSClient -->|SOAP Request| TomcatContainer

    style NetBeans fill:#90EE90
    style TomcatContainer fill:#FFD700
    style AccessDB fill:#ffe1e1
    style Browser1 fill:#e1f5ff
```

## 11. Technology Stack Overview

```mermaid
graph TB
    subgraph "Frontend Technologies"
        HTML[HTML5]
        CSS[CSS3<br/>Bootstrap 4]
        JS[JavaScript<br/>jQuery 1.11.1]
        JSP2[JSP 2.3<br/>Java Server Pages]
        AJAX[AJAX<br/>Autocomplete]
    end
    
    subgraph "Backend Technologies"
        Servlets[Java Servlets 3.1]
        JAXWS[JAX-WS 2.2<br/>SOAP Web Services]
        JavaMail[JavaMail API<br/>Email Notifications]
    end
    
    subgraph "Security Technologies"
        TomcatSec[Tomcat Security<br/>Form-Based Auth]
        Filters[Servlet Filters<br/>XSS, Cache Control]
    end
    
    subgraph "Data Access"
        JDBC[JDBC API]
        UCanAccessLib[UCanAccess 4.0.1<br/>MS Access Driver]
        Jackcess[Jackcess 2.1.6<br/>Java MS Access Library]
    end
    
    subgraph "Database"
        MSAccessDB[Microsoft Access<br/>airlines.accdb]
    end
    
    subgraph "Build & Deploy"
        ApacheAnt[Apache Ant<br/>Build Tool]
        Tomcat8[Apache Tomcat 8+<br/>Servlet Container]
        JDK8[JDK 8<br/>Java Runtime]
    end
    
    subgraph "Additional Libraries"
        Gson[Gson 2.2.2<br/>JSON Processing]
        CommonsLang[Commons Lang 2.6]
        CommonsLog[Commons Logging 1.1.1]
    end
    
    HTML --> JSP2
    CSS --> JSP2
    JS --> JSP2
    AJAX --> Servlets
    
    JSP2 --> Servlets
    Servlets --> JAXWS
    Servlets --> JavaMail
    Servlets --> Filters
    
    Filters --> TomcatSec
    
    Servlets --> JDBC
    JDBC --> UCanAccessLib
    UCanAccessLib --> Jackcess
    Jackcess --> MSAccessDB
    
    Servlets --> Gson
    Servlets --> CommonsLang
    
    ApacheAnt --> Tomcat8
    Tomcat8 --> JDK8
    
    style HTML fill:#e1f5ff
    style Servlets fill:#90EE90
    style MSAccessDB fill:#ffe1e1
    style Tomcat8 fill:#FFD700
```

## Summary

### Key Architectural Characteristics:

1. **Monolithic Architecture**: Single deployable WAR file
2. **MVC Pattern**: JSP (View), Servlets (Controller), Java Models (Model)
3. **In-Memory Data Caching**: All data loaded into ServletContext at startup
4. **Session-based Authentication**: Tomcat form-based authentication
5. **Synchronous Processing**: No async operations or message queues
6. **Single Database Connection**: Shared across all users via ServletContext
7. **Approval Workflow**: Two-phase commit pattern (Admin proposes, Manager approves)
8. **SOAP Web Services**: Legacy XML-based web services
9. **No Caching Layer**: Direct database queries (except initial load)
10. **No Load Balancing**: Single server deployment

### Limitations:

- ❌ Not horizontally scalable
- ❌ No microservices separation
- ❌ Legacy database (MS Access)
- ❌ No API versioning
- ❌ Limited monitoring/observability
- ❌ No containerization
- ❌ No CI/CD pipeline
- ❌ Session-based (not stateless)
- ❌ No real-time updates
- ❌ No mobile app support

