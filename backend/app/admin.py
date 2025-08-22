from django.contrib import admin
from .models import Feature, Vote

@admin.register(Feature)
class FeatureAdmin(admin.ModelAdmin):
    list_display = ("title", "created_at")
    search_fields = ("title", "description")

@admin.register(Vote)
class VoteAdmin(admin.ModelAdmin):
    list_display = ("feature", "voter_id", "created_at")
    list_filter = ("feature", "created_at")