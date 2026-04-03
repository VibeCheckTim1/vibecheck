export interface PlaylistApi {
	id: number;
	name: string;
	songCount: number;
}

export class Playlist {
	id: number;
	name: string;
	songCount: number;

	constructor(apiObject: PlaylistApi) {
		this.id = apiObject.id;
		this.name = apiObject.name;
		this.songCount = apiObject.songCount;
	}
}
