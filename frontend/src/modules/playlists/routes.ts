import type {RouteRecordRaw} from "vue-router";
import PlaylistDetailsView from "./views/PlaylistDetailsView.vue";

export const playlistRoutes: RouteRecordRaw[] = [
	{
		path: "/playlists/:playlistId",
		name: "playlistDetails",
		component: PlaylistDetailsView,
	},
];
