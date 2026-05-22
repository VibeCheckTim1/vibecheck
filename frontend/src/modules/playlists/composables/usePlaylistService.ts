import {useHttpClient} from "../../../composables/useHttpClient.ts";
import {Playlist, type PlaylistApi} from "../../../entities/playlist.ts";

export interface CreatePlaylistRequest {
	name: string;
	isFavorite: boolean;
	isPublic: boolean;
}

export interface UpdatePlaylistRequest {
	name: string;
	isFavorite: boolean;
	isPublic: boolean;
}

export function usePlaylistService() {
	const {httpDelete, httpGet, httpPatch, httpPost} = useHttpClient();

	async function createPlaylist(body: CreatePlaylistRequest): Promise<Playlist> {
		const data = await httpPost<PlaylistApi>("/playlists", body);
		return new Playlist(data);
	}

	async function getUserPlaylists(userId: number): Promise<Playlist[]> {
		const data = await httpGet<PlaylistApi[]>(`/users/${String(userId)}/playlists`);
		return data.map((playlist) => new Playlist(playlist));
	}

	async function getPlaylist(playlistId: number): Promise<Playlist> {
		const data = await httpGet<PlaylistApi>(`/playlists/${String(playlistId)}`);
		return new Playlist(data);
	}

	async function updatePlaylist(playlistId: number, body: UpdatePlaylistRequest): Promise<Playlist> {
		const data = await httpPatch<PlaylistApi>(`/playlists/${String(playlistId)}`, body);
		return new Playlist(data);
	}

	async function deletePlaylist(playlistId: number): Promise<void> {
		await httpDelete<void>(`/playlists/${String(playlistId)}`);
	}

	return {
		createPlaylist,
		getUserPlaylists,
		getPlaylist,
		updatePlaylist,
		deletePlaylist,
	};
}
