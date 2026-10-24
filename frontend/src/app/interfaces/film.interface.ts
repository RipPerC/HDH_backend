export interface Film {
  title: string;
  year: number;
  downloaded: boolean;
  director?: string;
  audio?: string;
  subtitle?: string;
  format?: string;
  id?: number;
}
