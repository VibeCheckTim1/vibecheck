import type {RouteRecordRaw} from "vue-router";
import SearchView from "./views/SearchView.vue";

export const searchRoutes: RouteRecordRaw[] = [
	{
		path: "/search",
		name: "search",
		component: SearchView,
	},
];
