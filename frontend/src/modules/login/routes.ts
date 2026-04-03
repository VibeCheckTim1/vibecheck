import type {RouteRecordRaw} from "vue-router";
import LoginView from "./views/LoginView.vue";
import {useState} from "../../composables/useState.ts";

export const loginRoutes: RouteRecordRaw[] = [
	{
		path: "/",
		name: "login",
		component: LoginView,
		beforeEnter: () => {
			const {isLoggedIn} = useState();

			if (isLoggedIn.value) {
				return {name: "home"};
			}

			return;
		}
	},
];
