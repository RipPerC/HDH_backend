export interface Film {
  id?: number;
  title: string;
  year: number;
  downloaded: boolean;
  director?: string;
  audio?: string;
  subtitle?: string;
  format?: string;
}

export class FilmModel implements Film {
  constructor(
    public title: string,
    public year: number,
    public downloaded: boolean = false,
    public director?: string,
    public audio?: string,
    public subtitle?: string,
    public format?: string,
    public id?: number
  ) {}
}
