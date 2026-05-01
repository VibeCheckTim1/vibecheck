import {useHttpClient} from "../../../composables/useHttpClient";
import {useFollowingStore} from "../stores/useFollowingStore.ts";
import {storeToRefs} from "pinia";

export interface FollowRequestRequest {
    "receiverId": Number
}

type FollowActionResult = 'FOLLOW' | 'FOLLOWING' | 'PENDING';
type FollowRequestUserResponse = 'ACCEPT' | 'DECLINE';

export interface FollowActionResponse {
    result: FollowActionResult
}

export interface FollowRequestActionRequest {
    action: FollowRequestUserResponse 
}

export interface FollowRequestResponse {
    idRequest: number;
    senderId: number;
    senderName: string;
    senderLastName: string;
    avatarUrl: string;
    createdAt: string;
}

export interface FollowStatsResponse {
    followersCount: number,
    followingCount: number
}

const apiUrl = "followRequest";


export function useFollowingService(receiverId?: number) {
    const { httpGet, httpPatch, httpPost, httpDelete } = useHttpClient();
    const followingStore = useFollowingStore();
    const {followRequests} = storeToRefs(followingStore);

    async function createFollowRequest(followRequest: FollowRequestRequest): Promise<FollowActionResponse> {
        return await httpPost<FollowActionResponse>(apiUrl, followRequest);
    }

    async function cancelFollowRequest(): Promise<void> {
        await httpDelete<void>(`${apiUrl}/${receiverId}`);
    }

    async function unfollow(): Promise<void> {
        await httpDelete<void>(`${apiUrl}/unfollow/${receiverId}`);
    }

    async function getFollowStatus(): Promise<FollowActionResponse> {
        return await httpGet<FollowActionResponse>(`${apiUrl}/getFollowStatus/${receiverId}`);
    }

    async function getAllFollowRequests(forceFetch = false): Promise<FollowRequestResponse[]> {
        if (forceFetch || followRequests.value.length === 0) {
            const data = await httpGet<FollowRequestResponse[]>(`${apiUrl}/getAll`);
            followingStore.setFollowRequests(data);
        }

        return followRequests.value;
    }

    async function acceptOrDeclineFollowRequest(requestId: number, body: FollowRequestActionRequest) {
        await httpPatch<void>(`${apiUrl}/acceptOrDeclineFollowRequest/${requestId}`, body);
    }

    async function getFollowStats(): Promise<FollowStatsResponse> {
        const data = await httpGet<FollowStatsResponse>(`${apiUrl}/stats`);
        return data;
    } 


    return {
        createFollowRequest,
        cancelFollowRequest,
        unfollow,
        getFollowStatus,
        getAllFollowRequests,
        acceptOrDeclineFollowRequest,
        getFollowStats
    }



}
