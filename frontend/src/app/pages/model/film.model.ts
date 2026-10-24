export class Film {
  constructor(
    public title: string,
    public year: number,
    public downloaded: boolean,
    public director?: string,
    public audio?: string,
    public subtitle?: string,
    public format?: string,
    public id?: number
  ) {}
}
