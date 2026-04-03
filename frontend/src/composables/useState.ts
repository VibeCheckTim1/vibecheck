import {computed, type Ref, ref} from "vue";
import type {User} from "../entities/user.ts";

const currentUser = ref<User | null>(null);

export function useState() {
	const isLoggedIn = computed(() => !!currentUser.value);

	function setUser(user: User) {
		currentUser.value = user;
	}

	return {
		isLoggedIn,
		currentUser: currentUser as Ref<User | null>,
		setUser,
	};
}
