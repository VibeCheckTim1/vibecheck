export interface PlaylistApi {
	id: number;
	name: string;
	isFavorite: boolean;
	isPublic: boolean;
	songCount: number;
	userId: number;
	username: string;
}

export class Playlist {
	id: number;
	name: string;
	isFavorite: boolean;
	isPublic: boolean;
	songCount: number;
	userId: number;
	username: string;

	constructor(apiObject: PlaylistApi) {
		this.id = apiObject.id;
		this.name = apiObject.name;
		this.isFavorite = apiObject.isFavorite;
		this.isPublic = apiObject.isPublic;
		this.songCount = apiObject.songCount;
		this.userId = apiObject.userId;
		this.username = apiObject.username;
	}
}
