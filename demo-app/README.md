# Demo Application

This is a simple demo application to test with the API Automation Testing Framework.

## 🏗️ Architecture

```
Demo App:
├── Backend API (Spring Boot)
│   └── Port: 8080
│   └── Endpoints: /api/users, /api/health
│
└── Frontend (HTML/JS)
    └── Port: 3000 (via Python server)
    └── Connects to: http://localhost:8080
```

## 🚀 Quick Start

### 1. Start the Backend API

```bash
cd demo-app/backend
mvn spring-boot:run
```

The API will start on **http://localhost:8080**

**API Endpoints:**
- `GET /api/users` - Get all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create new user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/health` - Health check

### 2. Start the Frontend

In a **new terminal**:

```bash
cd demo-app/frontend
python3 -m http.server 3000
```

The UI will be available at **http://localhost:3000**

### 3. Test the Application

Open your browser and go to:
- **Frontend**: http://localhost:3000
- **API Health**: http://localhost:8080/api/health
- **API Users**: http://localhost:8080/api/users

## 🧪 Now Run Your Tests!

With both services running, update your test framework configuration:

### Update Config File

Edit `src/test/resources/config/dev.properties`:

```properties
api.base.url=http://localhost:8080/api
ui.base.url=http://localhost:3000
browser=chromium
headless=false
```

### Run Tests

```bash
# From the main project directory (not demo-app)
cd ../..

# Run all tests against your local app
mvn test

# Run API tests only
mvn test -Dtest=SampleApiTest

# Run UI tests only
mvn test -Dtest=SampleUiTest

# Run in headed mode to see the browser
mvn test -Dheadless=false
```

## 📝 Sample Test Data

The backend starts with 3 users:
1. John Doe (Admin)
2. Jane Smith (User)
3. Bob Johnson (User)

You can add more through the UI or API!

## 🛑 Stop the Services

**Backend:**
- Press `Ctrl+C` in the backend terminal

**Frontend:**
- Press `Ctrl+C` in the frontend terminal

## 🎯 What to Test

### API Testing Ideas
- ✅ GET all users
- ✅ GET user by ID
- ✅ CREATE new user
- ✅ UPDATE existing user
- ✅ DELETE user
- ✅ Handle 404 for non-existent user

### UI Testing Ideas
- ✅ Load homepage
- ✅ Display user list
- ✅ Add new user via form
- ✅ Delete user
- ✅ Verify user card elements
- ✅ Test form validation

## 🔧 Troubleshooting

### Backend won't start
```bash
# Check if port 8080 is in use
lsof -ti:8080

# Kill the process if needed
kill -9 $(lsof -ti:8080)
```

### Frontend won't start
```bash
# Check if port 3000 is in use
lsof -ti:3000

# Kill the process if needed
kill -9 $(lsof -ti:3000)

# Or use a different port
python3 -m http.server 3001
```

### CORS Errors
The backend has CORS enabled (`@CrossOrigin(origins = "*")`), so this shouldn't be an issue.

## 📚 Technology Stack

**Backend:**
- Java 17+
- Spring Boot 3.2.0
- Maven

**Frontend:**
- HTML5
- CSS3
- Vanilla JavaScript
- No frameworks needed!

## 🎨 Features

✅ **Full CRUD operations**
✅ **RESTful API**
✅ **Modern UI with animations**
✅ **Responsive design**
✅ **Real-time updates**
✅ **Error handling**
✅ **In-memory storage (no database needed)**

---

**Now you have a real application to test! 🎉**
