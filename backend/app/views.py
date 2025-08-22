import serializer
from django.db import IntegrityError, models
from django.db.migrations import serializer
from django.db.models import Count
from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from backend.app.models import Feature, Vote
from backend.app.serializers import FeatureSerializer, VoteSerializer


class FeatureViewSet(viewsets.ModelViewSet):
    serializer_class = FeatureSerializer

    def get_queryset(self):
        return Feature.objects.annotate(voteCount=Count("vote")).order_by(
            models.F("voteCount").desc(), "created_at")

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        if not serializer.is_valid():
            return Response(
            {"error": "Validation failed.", "code": "validation_error", "details": serializer.errors},
            status=status.HTTP_400_BAD_REQUEST,       )
        self.perform_create(serializer)
        headers = self.get_success_headers(serializer.data)
        return Response(serializer.data, status=status.HTTP_201_CREATED, headers=headers)


def retrieve(self, request, *args, **kwargs):
    return super().retrieve(request, *args, **kwargs)


@action(detail=True, methods=["post"], serializer_class=VoteSerializer)
def upvote(self, request, pk=None):
    if not serializer.is_valid():
        return Response({"error": "Voter ID is required.", "code": "validation_error"}, status=status.HTTP_400_BAD_REQUEST)
    voter_id = serializer.validated_data["voter_id"]

    try:
        Vote.objects.create(feature_id=pk, voter_id=voter_id)
        feature = self.get_queryset().get(pk=pk)
        return Response(self.get_serializer(feature).data, status=status.HTTP_201_CREATED)
    except IntegrityError:
        return Response({"already_upvoted": True}, status=status.HTTP_200_OK)
    except Feature.DoesNotExist:
        return Response({"error": "Feature not found.", "code": "not_found"}, status=status.HTTP_404_NOT_FOUND)
