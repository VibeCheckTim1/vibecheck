import { useHttpClient } from "../../../composables/useHttpClient";

export interface FollowRequestRequest {
    "receiverId" : Number
}

export interface FollowActionResponse {
    "result": String
}


export function useFollowingService() {
    const { /* httpGet, httpPatch,  */httpPost/* , httpDelete  */} = useHttpClient();

    async function createFollowRequest(followRequest: FollowRequestRequest): Promise<FollowActionResponse> {
        const data = await httpPost<FollowActionResponse>(`followRequest`, followRequest);
        return data;
    }

    return {
        createFollowRequest
    }
        


}