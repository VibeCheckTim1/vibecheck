import { useHttpClient } from "../../../composables/useHttpClient";

export interface FollowRequestRequest {
    "receiverId": Number
}

type FollowActionResult = 'FOLLOW' | 'FOLLOWING' | 'PENDING';

export interface FollowActionResponse {
    result: FollowActionResult
}

export interface FollowRequestResponse {
    idRequest: number;
    senderId: number;
    senderName: string;
    senderLastName: string;
    createdAt: string;
}

const apiUrl = "followRequest";


export function useFollowingService(receiverId?: number) {
    const { httpGet, httpPatch, httpPost, httpDelete } = useHttpClient();

    async function createFollowRequest(followRequest: FollowRequestRequest): Promise<FollowActionResponse> {
        const data = await httpPost<FollowActionResponse>(apiUrl, followRequest);
        return data;
    }

    async function cancelFollowRequest(): Promise<void> {
        await httpDelete<void>(`${apiUrl}/${receiverId}`);
    }

    async function unfollow(): Promise<void> {
        await httpDelete<void>(`${apiUrl}/unfollow/${receiverId}`);
    }

    async function getFollowStatus(): Promise<FollowActionResponse> {
        const data = await httpGet<FollowActionResponse>(`${apiUrl}/getFollowStatus/${receiverId}`);
        return data;
    }

    async function getAllFollowRequests(): Promise<FollowRequestResponse[]> {
        const data = await httpGet<FollowRequestResponse[]>(`${apiUrl}/getAll`);
        return data;
    }

    async function acceptFollowRequest(requestId: number) {
        await httpPatch<void>(`${apiUrl}/acceptFollowRequest/${requestId}`);
    }


    return {
        createFollowRequest,
        cancelFollowRequest,
        unfollow,
        getFollowStatus,
        getAllFollowRequests,
        acceptFollowRequest
    }



}