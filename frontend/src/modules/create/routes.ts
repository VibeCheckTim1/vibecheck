import type {RouteRecordRaw} from "vue-router";
import CreateView from "./views/CreateView.vue";

export const createRoutes: RouteRecordRaw[] = [
	{
		path: "/create",
		name: "create",
		component: CreateView,
	},
];
