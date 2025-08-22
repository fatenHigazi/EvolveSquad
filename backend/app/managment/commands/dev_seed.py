from django.core.management.base import BaseCommand
from django.db import transaction
from app.models import Feature


class Command(BaseCommand):
    help = "Seeds the database with initial development features."

    @transaction.atomic
    def handle(self, *args, **options):
        self.stdout.write("Seeding initial features...")
        features_to_create = [
            {"title": "Implement User Authentication", "description": "Add a login/register system for users."},
            {"title": "Dark Mode", "description": "A popular request, let's add a dark theme for the UI."},
            {"title": "Push Notifications", "description": "Notify users of new upvotes on their features."},
        ]

        for data in features_to_create:
            if not Feature.objects.filter(title=data["title"]).exists():
                Feature.objects.create(**data)
                self.stdout.write(self.style.SUCCESS(f'Successfully created feature: "{data["title"]}"'))
            else:
                self.stdout.write(self.style.WARNING(f'Feature already exists, skipping: "{data["title"]}"'))

        self.stdout.write(self.style.SUCCESS("\nDatabase seeding complete."))