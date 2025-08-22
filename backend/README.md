Voting System
This project is a minimal, end-to-end voting system built for showcasing a modern full-stack development workflow. It allows users to propose features and upvote existing ones. The system consists of a REST API backend and a native Android mobile client.

<br>

<br>

Stack
Backend: Python 3.x, Django, Django REST Framework

Database: PostgreSQL

Mobile App: Android, Kotlin, Jetpack Compose

<br>

<br>

Quickstart
There are two ways to get the project running. Docker Compose is recommended for the fastest setup.

A) Using Docker Compose (Recommended) 🐳
Configure Environment: Copy the example file and set your database credentials.

Bash

cp backend/.env.example backend/.env
# Edit backend/.env with your Postgres user/password
Start Services: Build and start the backend and database containers.

Bash

docker compose up --build
Setup Database: Run migrations and seed initial data in the backend container.

Bash

docker compose exec backend python manage.py migrate
docker compose exec backend python manage.py dev_seed
Your backend API is now running at http://localhost:8000.

B) Running Locally 💻
Set up Backend:

Navigate to the backend directory.

Create and activate a Python virtual environment.

Install dependencies.

Bash

cd backend
python -m venv venv
source venv/bin/activate  # macOS/Linux
# venv\Scripts\activate.bat # Windows
pip install -r requirements.txt
Run PostgreSQL: Ensure you have a local PostgreSQL instance running. The backend will attempt to connect to it using the environment variables below.

Configure and Run: Create a .env file in the backend directory with your database details, then run migrations and start the server.

Bash

# Sample .env
# POSTGRES_DB=voting_system
# POSTGRES_USER=youruser
# POSTGRES_PASSWORD=yourpassword

python manage.py migrate
python manage.py dev_seed # Optional: seed with initial features
python manage.py runserver
<br>

<br>

API Endpoints
The API is fully documented with a swagger-ui interface available at http://localhost:8000/api/schema/swagger/.

List all features:
GET http://localhost:8000/api/features/

Bash

curl http://localhost:8000/api/features/
Create a new feature:
POST http://localhost:8000/api/features/

Bash

curl -X POST -H "Content-Type: application/json" -d '{"title":"My New Feature","description":"This is a cool idea."}' http://localhost:8000/api/features/
Upvote a feature:
POST http://localhost:8000/api/features/<id>/upvote/

Bash

curl -X POST -H "Content-Type: application/json" -d '{"voter_id":"user_123"}' http://localhost:8000/api/features/1/upvote/
<br>

<br>

Android App
To run the Android client:

Open the androidApp folder in Android Studio.

The app is configured to connect to the backend running on the Android emulator's host machine. The BASE_URL is set to http://10.0.2.2:8000/api/. This IP address is a special alias for localhost inside the Android emulator.

Run the app on an emulator or a physical device with USB debugging enabled.

<br>

<br>

Running Tests
To run the backend tests:

Bash

docker compose exec backend pytest -q
<br>

<br>

Troubleshooting
Database Connection: Check that your backend/.env file has the correct database credentials.

CORS Errors: If you're accessing the API from a different host (e.g., a web browser or a specific emulator), add its URL to the CORS_ALLOWED_ORIGINS list in backend/project/settings.py.

Emulator Host: If your Android emulator cannot connect, ensure the backend is running and that the BASE_URL in Api.kt is correctly pointing to your host's IP address.

<br>

<br>

Future Work & Limitations
Full Authentication: The current system uses a simple voter_id string for identifying users. Full user registration and authentication should be implemented.

Error Handling: The current error handling is minimal. More specific error messages and a consistent API error structure would improve client-side robustness.

UI Polish: The Android app is a minimal implementation without any advanced styling.

Web Client: A web-based frontend could be added to complement the native mobile app.