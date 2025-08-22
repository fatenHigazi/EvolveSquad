from django.db import models

class Feature(models.Model):
    title = models.CharField(max_length=100)
    description = models.TextField(max_length=1000, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.title

    class Meta:
        ordering = ["-created_at"]

class Vote(models.Model):
    feature = models.ForeignKey(Feature, on_delete=models.CASCADE)
    voter_id = models.CharField(max_length=255)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"Vote for {self.feature.title} by {self.voter_id}"

    class Meta:
        unique_together = ("feature", "voter_id")