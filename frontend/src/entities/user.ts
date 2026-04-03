export interface UserApi {
	idUser: number;
	email: string;
	username: string;
	tstamp: string;
}

export class User {
	idUser: number;
	email: string;
	username: string;
	tstamp: Date;

	constructor(apiObject: UserApi) {
		this.idUser = apiObject.idUser;
		this.email = apiObject.email;
		this.username = apiObject.username;
		this.tstamp = new Date(apiObject.tstamp);
	}
}
