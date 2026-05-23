import { useHttpClient } from "../../../composables/useHttpClient";

const apiUrl = "songs";

export function useSpotifyService() {
    const { httpPost } = useHttpClient();

    async function songLike(spotifySongId: string): Promise<void> {

        await httpPost<void>(`${apiUrl}/${spotifySongId}/like`);
    }

    return {
        songLike
    }
}