import pytest
from rest_framework.test import APIClient
from model_bakery import baker
from app.models import Feature, Vote

@pytest.fixture
def api_client():
    return APIClient()

@pytest.fixture
def feature_factory():
    """Returns a factory function to create Feature instances."""
    return baker.make

@pytest.fixture
def vote_factory():
    """Returns a factory function to create Vote instances."""
    return baker.make