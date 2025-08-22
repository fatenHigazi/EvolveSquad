from rest_framework import serializers
from backend.app.models import Feature


class FeatureSerializer(serializers.ModelSerializer):
    voteCount = serializers.IntegerField(read_only=True)

    class Meta:
        model = Feature
        fields = ["id", "title", "description", "created_at", "voteCount"]
        read_only_fields = ["created_at", "voteCount"]

    def validate_title(self, value):
        if not (3 <= len(value) <= 100):
            if value.strip() == "":
                raise serializers.ValidationError("Title cannot be empty.")
        return value

    def validate_description(self, value):
        if len(value) > 1000:
            raise serializers.ValidationError("Description cannot exceed 1000 characters.")
        return value

class VoteSerializer(serializers.Serializer):
    voter_id = serializers.CharField(max_length=255)