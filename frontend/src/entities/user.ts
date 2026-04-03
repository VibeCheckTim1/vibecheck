export interface UserApi {
	id: string;
	firstName: string;
	lastName: string;
	username: string;
	email: string;
}

export class User {
	id: string;
	firstName: string;
	lastName: string;
	username: string;
	email: string;

	constructor(apiObject: UserApi) {
		this.id = apiObject.id;
		this.firstName = apiObject.firstName;
		this.lastName = apiObject.lastName;
		this.username = apiObject.username;
		this.email = apiObject.email;
	}
}
