import type {RouteRecordRaw} from "vue-router";
import LoginView from "./views/LoginView.vue";

export const loginRoutes: RouteRecordRaw[] = [
	{
		path: "/",
		name: "login",
		component: LoginView,
	},
];
