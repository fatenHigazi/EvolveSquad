from django.db import IntegrityError
from django.db.models import Count
from rest_framework import viewsets, status
from rest_framework.decorators import action
from rest_framework.response import Response
from backend.app.models import Feature, Vote
from backend.app.serializers import FeatureSerializer, VoteSerializer

class FeatureViewSet(viewsets.ModelViewSet):
    serializer_class = FeatureSerializer

    def get_queryset(self):
        return Feature.objects.annotate(voteCount=Count("vote")).order_by("-voteCount", "-created_at")

    def retrieve(self, request, *args, **kwargs):
        instance = self.get_object()
        serializer = self.get_serializer(instance)
        return Response(serializer.data)

    @action(detail=True, methods=["post"], serializer_class=VoteSerializer)
    def upvote(self, request, pk=None):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        voter_id = serializer.validated_data["voter_id"]

        try:
            Vote.objects.create(feature_id=pk, voter_id=voter_id)
            feature = self.get_queryset().get(pk=pk)
            return Response(self.get_serializer(feature).data, status=status.HTTP_201_CREATED)
        except IntegrityError:
            return Response({"already_upvoted": True}, status=status.HTTP_200_OK)