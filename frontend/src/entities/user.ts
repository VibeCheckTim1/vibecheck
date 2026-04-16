export interface UserApi {
	idUser: number;
	firstName: string;
	lastName: string;
	avatarUrl: string | null;
	bio: string | null;
	email: string;
	username: string;
	isPrivate: boolean;
	tstamp: string;
}

export class User {
	idUser: number;
	firstName: string;
	lastName: string;
	avatarUrl: string | null;
	bio: string | null;
	email: string;
	username: string;
	isPrivate: boolean;
	tstamp: Date;

	constructor(apiObject: UserApi) {
		this.idUser = apiObject.idUser;
		this.firstName = apiObject.firstName;
		this.lastName = apiObject.lastName;
		this.avatarUrl = apiObject.avatarUrl;
		this.bio = apiObject.bio;
		this.email = apiObject.email;
		this.username = apiObject.username;
		this.isPrivate = apiObject.isPrivate;
		this.tstamp = new Date(apiObject.tstamp);
	}
}
