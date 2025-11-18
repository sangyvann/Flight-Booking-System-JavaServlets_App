# Original Flight Booking System - Architecture Diagrams

## 1. High-Level System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                          Turkish Airlines Flight Booking System                │
│                                (Original Architecture)                         │
└─────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐    HTTP Request/Response    ┌───────────────────────────────────┐
│                 │◄─────────────────────────────►│                                   │
│   Web Browser   │                              │        Apache Tomcat              │
│   (Client)      │                              │      Java EE Container            │
│                 │                              │                                   │
└─────────────────┘                              └───────────────────────────────────┘
                                                           │
                                                           │
                ┌──────────────────────────────────────────┼──────────────────────────────────────────┐
                │                                          │                                          │
                ▼                                          ▼                                          ▼
┌─────────────────────────┐              ┌─────────────────────────┐              ┌─────────────────────────┐
│                         │              │                         │              │                         │
│    Presentation Layer   │              │    Business Layer       │              │     Data Layer          │
│       (View)            │              │    (Controller)         │              │   (Model/Persistence)   │
│                         │              │                         │              │                         │
│  • JSP Pages            │              │  • Java Servlets        │              │  • MS Access Database  │
│  • Static Assets        │              │  • Business Logic       │              │  • Direct JDBC          │
│  • Templates            │              │  • Security Filters     │              │  • Data Models          │
│  • JavaScript/CSS       │              │  • Session Management   │              │                         │
│                         │              │                         │              │                         │
└─────────────────────────┘              └─────────────────────────┘              └─────────────────────────┘
```

## 2. Detailed Component Architecture

```
┌───────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    TOMCAT WEB CONTAINER                                                   │
├───────────────────────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                                           │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                   WEB FILTERS                                                       │ │
│  │                                                                                                     │ │
│  │   SecurityFilter              XSSFilter                  ServletContextListener                    │ │
│  │   └─ Cache Control             └─ XSS Prevention         └─ Application Initialization             │ │
│  │                                                                                                     │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                │                                                         │
│                                                ▼                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                   SERVLET LAYER                                                     │ │
│  │                                                                                                     │ │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐           │ │
│  │  │  Authentication  │  │   Admin Layer    │  │  Manager Layer   │  │  Customer Layer  │           │ │
│  │  │                  │  │                  │  │                  │  │                  │           │ │
│  │  │ • LoginManager   │  │ • ChangeFeatures │  │ • ApproveFeatures│  │ • SearchFlights  │           │ │
│  │  │ • LogoutManager  │  │ • SetSeats       │  │ • ApproveSeats   │  │ • BookFlight     │           │ │
│  │  │ • CustomerMgr    │  │                  │  │ • DisapproveXXX  │  │ • CurrentBooking │           │ │
│  │  │                  │  │                  │  │                  │  │ • ChooseFlight   │           │ │
│  │  └──────────────────┘  └──────────────────┘  └──────────────────┘  └──────────────────┘           │ │
│  │                                                                                                     │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                │                                                         │
│                                                ▼                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                   MODEL LAYER                                                       │ │
│  │                                                                                                     │ │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐           │ │
│  │  │   Data Models    │  │  Business Logic  │  │   Utilities      │  │  Web Services    │           │ │
│  │  │                  │  │                  │  │                  │  │                  │           │ │
│  │  │ • Customer       │  │ • FBS (Main)     │  │ • OriginComplete │  │ • PriceAndSeats  │           │ │
│  │  │ • Flight         │  │ • Features       │  │ • SContextList   │  │   (SOAP)         │           │ │
│  │  │ • Booking        │  │ • Seat           │  │                  │  │                  │           │ │
│  │  │ • Features       │  │ • Employee       │  │                  │  │                  │           │ │
│  │  │ • Person         │  │                  │  │                  │  │                  │           │ │
│  │  └──────────────────┘  └──────────────────┘  └──────────────────┘  └──────────────────┘           │ │
│  │                                                                                                     │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
│                                                │                                                         │
│                                                ▼                                                         │
│  ┌─────────────────────────────────────────────────────────────────────────────────────────────────────┐ │
│  │                                   VIEW LAYER                                                        │ │
│  │                                                                                                     │ │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐           │ │
│  │  │   JSP Pages      │  │    Templates     │  │  Static Assets   │  │   Configuration  │           │ │
│  │  │                  │  │                  │  │                  │  │                  │           │ │
│  │  │ • home.jsp       │  │ • AdminHeader    │  │ • CSS Files      │  │ • web.xml        │           │ │
│  │  │ • BookFlight.jsp │  │ • CustomerHeader │  │ • JavaScript     │  │ • context.xml    │           │ │
│  │  │ • ShowFlights    │  │ • AdminPanel     │  │ • Images         │  │ • MANIFEST.MF    │           │ │
│  │  │ • CurrentBooking │  │ • ManagerPanel   │  │ • Autocomplete   │  │                  │           │ │
│  │  │ • ErrorPage      │  │ • Footer         │  │                  │  │                  │           │ │
│  │  └──────────────────┘  └──────────────────┘  └──────────────────┘  └──────────────────┘           │ │
│  │                                                                                                     │ │
│  └─────────────────────────────────────────────────────────────────────────────────────────────────────┘ │
└───────────────────────────────────────────────────────────────────────────────────────────────────────────┘
                                                │
                                                ▼
┌───────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   MS ACCESS DATABASE                                                     │
│                                                                                                           │
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐                 │
│  │     Tables       │  │     Tables       │  │     Tables       │  │     Tables       │                 │
│  │                  │  │                  │  │                  │  │                  │                 │
│  │   • Flights      │  │   • Customers    │  │   • Features     │  │   • Seats        │                 │
│  │   • Cities       │  │   • Bookings     │  │   • Employees    │  │   • Others       │                 │
│  │                  │  │                  │  │                  │  │                  │                 │
│  └──────────────────┘  └──────────────────┘  └──────────────────┘  └──────────────────┘                 │
└───────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

## 3. User Flow and Authentication Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   USER AUTHENTICATION FLOW                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│                 │       │                 │       │                 │       │                 │
│   User Access   │──────►│  Login.jsp      │──────►│  LoginManager   │──────►│ Role Detection  │
│   Application   │       │  (Form Auth)    │       │   (Servlet)     │       │ & Redirection   │
│                 │       │                 │       │                 │       │                 │
└─────────────────┘       └─────────────────┘       └─────────────────┘       └─────────────────┘
                                                                                       │
                                                                                       ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   ROLE-BASED REDIRECTION                                               │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘

      ADMIN ROLE                    MANAGER ROLE                    CUSTOMER ROLE
          │                             │                              │
          ▼                             ▼                              ▼
┌─────────────────┐             ┌─────────────────┐            ┌─────────────────┐
│                 │             │                 │            │                 │
│ ChangeFeatures  │             │ ApproveFeatures │            │ CurrentBooking  │
│     .jsp        │             │      .jsp       │            │     .jsp        │
│                 │             │                 │            │                 │
└─────────────────┘             └─────────────────┘            └─────────────────┘
          │                             │                              │
          ▼                             ▼                              ▼
┌─────────────────┐             ┌─────────────────┐            ┌─────────────────┐
│  Admin Actions  │             │ Manager Actions │            │Customer Actions │
│                 │             │                 │            │                 │
│ • Set Prices    │             │ • Approve       │            │ • Search Flights│
│ • Change Seats  │             │   Changes       │            │ • Book Flights  │
│ • Modify        │             │ • Review        │            │ • View Bookings │
│   Features      │             │   Settings      │            │ • Cancel Trips  │
│                 │             │                 │            │                 │
└─────────────────┘             └─────────────────┘            └─────────────────┘
```

## 4. Data Flow Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    REQUEST PROCESSING FLOW                                             │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘

User Request
     │
     ▼
┌─────────────────┐
│   HTTP Request  │
│   to Tomcat     │
└─────────────────┘
     │
     ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│ SecurityFilter  │───►│   XSSFilter     │───►│ ServletContext  │
│ (Cache Control) │    │ (XSS Prevention)│    │   Listener      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                      │
                                                      ▼
                                             ┌─────────────────┐
                                             │ Load Application│
                                             │ Data into       │
                                             │ ServletContext  │
                                             └─────────────────┘
                                                      │
                                                      ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   SERVLET DISPATCH                                                     │
│                                                                                                         │
│   URL Pattern              Servlet Class               Functionality                                   │
│   ─────────────────────────────────────────────────────────────────────────────────────────────────── │
│   /Login                 → LoginManager              → User Authentication                            │
│   /Logout                → LogoutManager             → User Logout                                   │
│   /SearchFlights.do      → customer.SearchFlights    → Flight Search                                │
│   /BookFlight.do         → customer.BookFlight       → Flight Booking                               │
│   /CurrentBooking.do     → customer.CurrentBooking   → View User Bookings                           │
│   /ChangeFeatures.do     → admin.ChangeFeatures      → Modify Flight Features                       │
│   /ApproveFeatures.do    → manager.ApproveFeatures   → Approve Admin Changes                        │
│   /SetSeats.do           → admin.SetSeats            → Set Seat Configuration                       │
│   /OriginCompleter       → OriginCompleter           → AJAX City Autocomplete                       │
│   /PriceAndSeats         → webservices.PriceAndSeats → SOAP Web Service                             │
│                                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
                                                      │
                                                      ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    BUSINESS LOGIC                                                      │
│                                                                                                         │
│  ┌──────────────────┐           ┌──────────────────┐           ┌──────────────────┐                   │
│  │   Data Access    │           │  Business Rules  │           │   Response       │                   │
│  │                  │           │                  │           │   Generation     │                   │
│  │ • Database Conn  │──────────►│ • Validation     │──────────►│                  │                   │
│  │ • SQL Queries    │           │ • Calculations   │           │ • JSP Forward    │                   │
│  │ • UCanAccess     │           │ • Workflow       │           │ • JSON Response  │                   │
│  │   Driver         │           │ • State Mgmt     │           │ • Redirect       │                   │
│  │                  │           │                  │           │                  │                   │
│  └──────────────────┘           └──────────────────┘           └──────────────────┘                   │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
                                                      │
                                                      ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   DATABASE ACCESS                                                      │
│                                                                                                         │
│   Connection String: jdbc:ucanaccess://path/to/airlines.accdb                                         │
│                                                                                                         │
│   Tables Used:                                                                                         │
│   • Flights    → Flight information, schedules, pricing                                               │
│   • Customers  → User account information                                                             │
│   • Features   → Seat features, pricing tiers                                                         │
│   • Seats      → Individual seat assignments                                                          │
│   • Cities     → Available departure/arrival cities                                                   │
│                                                                                                         │
│   Data Operations:                                                                                     │
│   • SELECT    → Read flight data, customer info                                                       │
│   • INSERT    → New bookings, customer registration                                                   │
│   • UPDATE    → Modify features, approve changes                                                      │
│   • DELETE    → Cancel bookings (rare)                                                               │
│                                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

## 5. Security Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   SECURITY ARCHITECTURE                                                │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐
│   User Access   │
└─────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                  TOMCAT AUTHENTICATION                                                 │
│                                                                                                         │
│  Authentication Method: FORM                                                                           │
│  Configuration: web.xml                                                                               │
│                                                                                                         │
│  <login-config>                                                                                        │
│    <auth-method>FORM</auth-method>                                                                     │
│    <form-login-config>                                                                                │
│      <form-login-page>/WEB-INF/Login.jsp</form-login-page>                                           │
│      <form-error-page>/ErrorPage.jsp</form-error-page>                                               │
│    </form-login-config>                                                                               │
│  </login-config>                                                                                       │
│                                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    ROLE-BASED AUTHORIZATION                                            │
│                                                                                                         │
│  Roles Defined:                                Security Constraints:                                  │
│  • Admin                            <security-constraint>                                             │
│  • Manager                            <web-resource-collection>                                       │
│  • Customer                             <url-pattern>/ChangeFeatures.jsp</url-pattern>               │
│                                       </web-resource-collection>                                      │
│  User Database:                       <auth-constraint>                                               │
│  tomcat-users.xml                       <role-name>Admin</role-name>                                 │
│                                       </auth-constraint>                                              │
│  <user username="admin@email.com"    </security-constraint>                                          │
│        password="a"                                                                                   │
│        roles="Admin"/>                                                                                │
│                                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                      SECURITY FILTERS                                                  │
│                                                                                                         │
│  ┌──────────────────────────┐              ┌──────────────────────────┐                               │
│  │     SecurityFilter       │              │       XSSFilter          │                               │
│  │                          │              │                          │                               │
│  │ • Cache-Control headers  │              │ • Cross-Site Scripting   │                               │
│  │ • Pragma: no-cache       │              │   Prevention             │                               │
│  │ • Expires: 0             │              │ • Input Sanitization     │                               │
│  │ • Prevent back button    │              │ • Applied to specific    │                               │
│  │   caching                │              │   endpoints              │                               │
│  │                          │              │                          │                               │
│  └──────────────────────────┘              └──────────────────────────┘                               │
│                                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
         │
         ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   SQL INJECTION PREVENTION                                             │
│                                                                                                         │
│   Implementation: PreparedStatement usage in FBS.java                                                 │
│                                                                                                         │
│   Example:                                                                                             │
│   String q = "SELECT * FROM FLIGHTS WHERE DEPARTURECITY = ? AND ARRIVALCITY = ?";                    │
│   statement = con.prepareStatement(q);                                                                │
│   statement.setString(1, origin);                                                                     │
│   statement.setString(2, destination);                                                                │
│   ResultSet r = statement.executeQuery();                                                             │
│                                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

## 6. Session Management and State Flow

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   SESSION MANAGEMENT                                                   │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘

User Login
     │
     ▼
┌─────────────────┐
│ LoginManager    │
│ Servlet         │
└─────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                               APPLICATION CONTEXT LOADING                                              │
│                                                                                                         │
│  SContextListener.java:                                                                               │
│                                                                                                         │
│  1. Database Connection Establishment                                                                  │
│     └─ UCanAccess JDBC Driver                                                                         │
│     └─ Connection stored in ServletContext                                                            │
│                                                                                                         │
│  2. Data Loading into Application Scope:                                                              │
│     ├─ Features (seat configurations, pricing)                                                        │
│     ├─ Customers (user accounts)                                                                      │
│     ├─ Flights (flight schedules, availability)                                                       │
│     └─ FBS (main business logic class)                                                                │
│                                                                                                         │
│  3. Objects stored in ServletContext attributes:                                                      │
│     ├─ setAttribute("features", featuresList)                                                         │
│     ├─ setAttribute("customers", customersList)                                                       │
│     ├─ setAttribute("flights", flightsList)                                                           │
│     └─ setAttribute("fbs", fbsInstance)                                                               │
│                                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   USER SESSION STATE                                                   │
│                                                                                                         │
│  Role Detection (LoginManager.java):                                                                  │
│                                                                                                         │
│  if(request.isUserInRole("Admin")) {                                                                  │
│      response.sendRedirect("ChangeFeatures.jsp");                                                     │
│  }                                                                                                     │
│  else if(request.isUserInRole("Manager")) {                                                           │
│      response.sendRedirect("ApproveFeatures.jsp");                                                    │
│  }                                                                                                     │
│  else if(request.isUserInRole("Customer")) {                                                          │
│      // Set customer object in session                                                               │
│      HttpSession s = request.getSession();                                                            │
│      s.setAttribute("customer", customerObject);                                                      │
│      request.getRequestDispatcher("CurrentBooking.do").forward(request, response);                   │
│  }                                                                                                     │
│                                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                    STATE PERSISTENCE                                                   │
│                                                                                                         │
│  Session Timeout: -1 (Never expires)                                                                  │
│  <session-config>                                                                                      │
│    <session-timeout>-1</session-timeout>                                                              │
│  </session-config>                                                                                     │
│                                                                                                         │
│  Application State:                                                                                    │
│  • Loaded once at startup                                                                             │
│  • Shared across all user sessions                                                                    │
│  • Modified by admin/manager actions                                                                  │
│  • Persisted to database on changes                                                                   │
│                                                                                                         │
│  User Session State:                                                                                   │
│  • Customer object (for logged-in customers)                                                          │
│  • Shopping cart equivalent (selected flights)                                                        │
│  • User preferences and selections                                                                    │
│                                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

## 7. Web Services Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   SOAP WEB SERVICES                                                    │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐     SOAP/HTTP     ┌─────────────────┐     Method Call    ┌─────────────────┐
│                 │◄─────────────────►│                 │◄─────────────────►│                 │
│  External       │                   │  PriceAndSeats  │                   │   FBS Static    │
│  Client         │                   │  Web Service    │                   │   Methods       │
│  (WSTester)     │                   │                 │                   │                 │
└─────────────────┘                   └─────────────────┘                   └─────────────────┘

Web Service Configuration (web.xml):
<servlet>
    <servlet-name>PriceAndSeats</servlet-name>
    <servlet-class>com.sun.xml.ws.transport.http.servlet.WSServlet</servlet-class>
    <load-on-startup>1</load-on-startup>
</servlet>

<servlet-mapping>
    <servlet-name>PriceAndSeats</servlet-name>
    <url-pattern>/PriceAndSeats</url-pattern>
</servlet-mapping>

┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                  WEB SERVICE OPERATIONS                                                │
│                                                                                                         │
│  @WebService(serviceName = "PriceAndSeats")                                                           │
│  public class PriceAndSeats {                                                                         │
│                                                                                                         │
│      @WebMethod(operationName = "GetSeatPrice")                                                       │
│      public int getPrice(String origin, String destination) {                                         │
│          return FBS.getPrice(origin, destination);                                                    │
│      }                                                                                                 │
│                                                                                                         │
│      @WebMethod(operationName = "GetAvailableSeats")                                                  │
│      public int getSeats(String flightNumber, String date) {                                          │
│          return FBS.getSeats(flightNumber, date);                                                     │
│      }                                                                                                 │
│  }                                                                                                     │
│                                                                                                         │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘
```

## 8. Build and Deployment Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────────────────────────┐
│                                   BUILD SYSTEM (ANT)                                                   │
└─────────────────────────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐
│                 │        │                 │        │                 │        │                 │
│  Source Code    │───────►│   build.xml     │───────►│  Compiled WAR   │───────►│   Tomcat        │
│  (Java/JSP)     │        │   (Ant Build)   │        │   Application   │        │   Deployment    │
│                 │        │                 │        │                 │        │                 │
└─────────────────┘        └─────────────────┘        └─────────────────┘        └─────────────────┘

NetBeans IDE Integration:
├── nbproject/
│   ├── build-impl.xml
│   ├── project.properties
│   ├── project.xml
│   └── private/
│       ├── private.properties
│       └── private.xml

Build Artifacts:
├── build/
│   └── web/
│       ├── Compiled JSPs
│       ├── Java Classes
│       ├── Static Resources
│       └── WEB-INF/
│           ├── classes/
│           ├── lib/
│           └── web.xml

Deployment Structure:
TurkishAirlines.war
├── JSP files
├── Static assets
├── WEB-INF/
│   ├── classes/ (Java classes)
│   ├── lib/ (JAR dependencies)
│   ├── web.xml
│   └── sun-jaxws.xml
└── META-INF/
    ├── context.xml
    └── MANIFEST.MF
```

This comprehensive architecture documentation shows the original system's structure, data flow, security model, and deployment approach. The system follows traditional Java EE patterns with JSP/Servlet architecture, form-based authentication, and direct database access through JDBC.