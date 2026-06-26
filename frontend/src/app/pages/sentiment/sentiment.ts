import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Football } from '../../services/football';

type SentimentRecord = {
  id: number;
  fanName: string;
  team: string;
  message: string;
  score: number;
  label: 'POSITIVE' | 'NEUTRAL' | 'NEGATIVE';
  createdAt: string;
};

type SentimentSummary = {
  totalMentions: number;
  positiveMentions: number;
  neutralMentions: number;
  negativeMentions: number;
  positivityRate: number;
  negativityRate: number;
  overallMood: string;
  topPositiveTeam: string;
  topNegativeTeam: string;
};

@Component({
  selector: 'app-sentiment',
  imports: [CommonModule, FormsModule],
  templateUrl: './sentiment.html',
  styleUrl: './sentiment.scss',
})
export class Sentiment implements OnInit {
  records: SentimentRecord[] = [];
  summary: SentimentSummary | null = null;
  loadError = '';
  submitError = '';

  fanName = '';
  team = 'Argentina';
  message = '';
  submitting = false;

  constructor(private footballService: Football) {}

  ngOnInit(): void {
    this.loadSentimentData();
  }

  loadSentimentData(): void {
    this.loadError = '';
    this.footballService.getSentiments().subscribe({
      next: (data: any) => {
        this.records = Array.isArray(data) ? data : data?.value || [];
      },
      error: () => {
        this.records = [];
        this.loadError = 'Unable to load sentiment records. Please ensure backend is running on port 8080.';
      },
    });

    this.footballService.getSentimentSummary().subscribe({
      next: (data: any) => {
        this.summary = data;
      },
      error: () => {
        this.summary = null;
        this.loadError = 'Unable to load sentiment summary. Please ensure backend is running on port 8080.';
      },
    });
  }

  submit(): void {
    if (!this.team.trim() || !this.message.trim() || this.submitting) {
      return;
    }

    this.submitting = true;
    this.submitError = '';
    this.footballService
      .analyzeSentiment(this.fanName.trim(), this.team.trim(), this.message.trim())
      .subscribe({
        next: () => {
          this.message = '';
          this.submitting = false;
          this.loadSentimentData();
        },
        error: () => {
          this.submitting = false;
          this.submitError = 'Sentiment analysis failed. Please try again after confirming backend availability.';
        },
      });
  }

  sentimentWidth(count: number): string {
    if (!this.summary || this.summary.totalMentions === 0) {
      return '0%';
    }
    return `${Math.round((count * 100) / this.summary.totalMentions)}%`;
  }
}
