import type {RouteRecordRaw} from "vue-router";
import HomeView from "./views/HomeView.vue";

export const homeRoutes: RouteRecordRaw[] = [
	{
		path: "/home",
		name: "home",
		component: HomeView,
	},
];
