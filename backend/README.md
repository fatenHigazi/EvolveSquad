Voting System Backend
This project is the backend for a voting system, built with Django and Django REST Framework.

Setup
This project uses Docker to manage the backend and PostgreSQL database.

Create .env file:
Copy the contents from .env.example to a new file named .env in the /backend directory and fill in your PostgreSQL database credentials.

Start the Docker containers:

Bash

docker compose up --build
This command will build the backend image and start both the backend and db services.

Run Database Migrations:
Open a new terminal and run the following command to apply database migrations:

Bash

docker compose exec backend python manage.py migrate
Seed Initial Data (Optional):
To add sample data, run the seeding command:

Bash

docker compose exec backend python manage.py dev_seed
The API will be available at http://localhost:8000/api/.