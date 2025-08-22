import pytest
from django.urls import reverse
from rest_framework import status
from app.models import Feature, Vote


@pytest.mark.django_db
class TestFeatureAPI:

    def test_create_feature_and_list(self, api_client):
        """
        Test that a new feature can be created and appears in the list.
        """
        create_url = reverse("feature-list")
        data = {"title": "New Awesome Feature", "description": "This is a great new feature."}

        response = api_client.post(create_url, data, format="json")
        assert response.status_code == status.HTTP_201_CREATED
        assert "id" in response.data
        assert response.data["title"] == data["title"]

        list_url = reverse("feature-list")
        response = api_client.get(list_url)
        assert response.status_code == status.HTTP_200_OK
        assert len(response.data) == 1
        assert response.data[0]["title"] == data["title"]

    def test_upvote_idempotent_by_voter(self, api_client, feature_factory, vote_factory):
        """
        Test that a single voter cannot upvote the same feature more than once.
        """
        feature = feature_factory(Feature, title="Test Feature")
        upvote_url = reverse("feature-upvote", kwargs={"pk": feature.id})
        voter_id = "voter_123"
        data = {"voter_id": voter_id}

        # First upvote should succeed
        response = api_client.post(upvote_url, data, format="json")
        assert response.status_code == status.HTTP_201_CREATED
        assert response.data["voteCount"] == 1
        assert Vote.objects.filter(feature=feature, voter_id=voter_id).exists()

        # Second upvote should return 200 with the specified message
        response = api_client.post(upvote_url, data, format="json")
        assert response.status_code == status.HTTP_200_OK
        assert response.data == {"already_upvoted": True}
        assert Vote.objects.filter(feature=feature).count() == 1

    def test_vote_counts_sorting(self, api_client, feature_factory, vote_factory):
        """
        Test that features are sorted by vote count (desc) then creation date (desc).
        """
        # Create features with varying vote counts
        feature_a = feature_factory(Feature, title="Feature A")
        feature_b = feature_factory(Feature, title="Feature B")
        feature_c = feature_factory(Feature, title="Feature C")

        # Give votes: B gets 3, C gets 1, A gets 0
        for i in range(3):
            vote_factory(Vote, feature=feature_b, voter_id=f"voter_b_{i}")
        vote_factory(Vote, feature=feature_c, voter_id="voter_c_1")

        list_url = reverse("feature-list")
        response = api_client.get(list_url)

        assert response.status_code == status.HTTP_200_OK
        assert len(response.data) == 3

        # Check order
        assert response.data[0]["title"] == feature_b.title  # 3 votes
        assert response.data[1]["title"] == feature_c.title  # 1 vote
        assert response.data[2]["title"] == feature_a.title  # 0 votes

    def test_upvote_nonexistent_feature_404(self, api_client):
        """
        Test that upvoting a feature that does not exist returns a 404.
        """
        non_existent_id = 999
        upvote_url = reverse("feature-upvote", kwargs={"pk": non_existent_id})
        data = {"voter_id": "voter_404"}

        response = api_client.post(upvote_url, data, format="json")
        assert response.status_code == status.HTTP_404_NOT_FOUND