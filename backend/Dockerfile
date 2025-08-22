# Use the official Python image as a base
FROM python:3.10-slim

# Set environment variables for non-interactive use
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# Set the working directory in the container
WORKDIR /app

# Install dependencies
COPY poetry.lock pyproject.toml /app/
RUN pip install poetry
RUN poetry config virtualenvs.create false
RUN poetry install --no-root --only main

# Copy the project files into the container
COPY . /app/

# Expose port 8000 for the Django application
EXPOSE 8000

# Run the Django development server
CMD ["python", "manage.py", "runserver", "0.0.0.0:8000"]