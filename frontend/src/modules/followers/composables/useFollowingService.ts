import { useHttpClient } from "../../../composables/useHttpClient";

export interface FollowRequestRequest {
    "receiverId" : Number
}

export interface FollowActionResponse {
    "result": String
}

const apiUrl = "followRequest";


export function useFollowingService() {
    const { /* httpGet, httpPatch,  */httpPost, httpDelete } = useHttpClient();

    async function createFollowRequest(followRequest: FollowRequestRequest): Promise<FollowActionResponse> {
        const data = await httpPost<FollowActionResponse>(apiUrl, followRequest);
        return data;
    }


    async function cancelFollowRequest(receiverId: number): Promise<void> {
        await httpDelete<void>(`{{apiUrl}}/${String(receiverId)}`);
    }



    return {
        createFollowRequest,
        cancelFollowRequest
    }
        


}