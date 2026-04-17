<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useState } from '../../../composables/useState';
import PageHeaderComponent from "../../../components/PageHeaderComponent.vue";
import { useRouter } from 'vue-router';
import { ApiError } from '../../../composables/useHttpClient';
import { useToast } from '../../../composables/useToast';
import { useFollowingService, type FollowRequestResponse } from '../../followers/composables/useFollowingService';


const { currentUser } = useState();

const followRequests = ref<FollowRequestResponse[]>([]);

const now = ref(Date.now());

const router = useRouter();

const { showSuccess, showError } = useToast();


async function fetchAllFollowRequests() {
    if (!currentUser.value) return;
    const { getAllFollowRequests } = useFollowingService();
    followRequests.value = await getAllFollowRequests(true);
}

function timeAgo(dateStr: string) {
    const diff = now.value - new Date(dateStr).getTime();
    const minutes = Math.floor(diff / 60000);

    if (minutes < 1) return "just now";
    if (minutes < 60) return `${minutes} min ago`;

    const hours = Math.floor(minutes / 60);
    return `${hours} h ago`;
}

function goToProfile(userId: number) {
    router.push({
        name: "profile",
        params: { userId }
    });
}


async function acceptFollowRequest(requestId: number) {
    if (!currentUser.value) return;

    try {
        const { acceptOrDeclineFollowRequest } = useFollowingService();
        await acceptOrDeclineFollowRequest(requestId, { action: "ACCEPT" });
        showSuccess("Follow request accepted!");
        await fetchAllFollowRequests();
    }
    catch (error) {
        if (error instanceof ApiError) {
            showError(error.message);
        }
    }

}

async function declineFollowRequest(requestId: number) {
    if (!currentUser.value) return;

    try {
        const { acceptOrDeclineFollowRequest } = useFollowingService();
        await acceptOrDeclineFollowRequest(requestId, { action: "DECLINE" });
        showSuccess("Follow request declined!");
        await fetchAllFollowRequests();
    }
    catch (error) {
        if (error instanceof ApiError) {
            showError(error.message);
        }
    }

}

onMounted(async () => {
    await fetchAllFollowRequests();
});
</script>



<template>
    <PageHeaderComponent>
        Follow Requests
    </PageHeaderComponent>

    <div class="follow-requests-page">
        <div v-if="followRequests.length === 0" class="empty-state">
            <p class="empty-title">No follow requests</p>
            <p class="empty-subtitle">When someone sends you a follow request, it will appear here.</p>
        </div>

        <div v-else class="requests-list">
            <div v-for="req in followRequests" :key="req.idRequest" class="request-card" @click="goToProfile(req.senderId)">
                <div class="request-user-section">
                    <div class="avatar-placeholder">
                        {{ req.senderName.charAt(0) }}{{ req.senderLastName.charAt(0) }}
                    </div>

                    <div class="request-user-info">
                        <span class="request-name">{{ req.senderName }} {{ req.senderLastName }}</span>
                        <span class="request-time">{{ timeAgo(req.createdAt) }}</span>
                    </div>
                </div>

                <div class="request-actions">
                    <button class="primary-button" @click.stop @click="acceptFollowRequest(req.idRequest)">Accept</button>
                    <button class="secondary-button" @click.stop @click="declineFollowRequest(req.idRequest)">Decline</button>
                </div>
            </div>
        </div>
    </div>
</template>


<style scoped>
.follow-requests-page {
    max-width: 720px;
    margin: 0 auto;
    padding: 1.5rem 1rem 2rem;
}

.requests-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.request-card {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1rem;
    padding: 1rem 1.25rem;
    border-radius: 18px;
    background: #ffffff;
    border: 1px solid #ede9fe;
}

.request-user-section {
    display: flex;
    align-items: center;
    gap: 0.9rem;
    min-width: 0;
}


.avatar-placeholder {
    width: 52px;
    height: 52px;
    border-radius: 50%;
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #8b5cf6, #6d28d9);
    color: white;
    font-weight: 700;
    font-size: 1rem;
    letter-spacing: 0.5px;
    box-shadow: 0 6px 16px rgba(109, 40, 217, 0.25);
}

.request-user-info {
    display: flex;
    flex-direction: column;
    min-width: 0;
}

.request-name {
    font-size: 1rem;
    font-weight: 700;
    color: #1f2937;
    line-height: 1.2;
}

.request-time {
    margin-top: 0.2rem;
    font-size: 0.88rem;
    color: #6b7280;
}

.request-actions {
    display: flex;
    align-items: center;
    gap: 0.75rem;
    flex-shrink: 0;
}

.request-btn {
    border: none;
    border-radius: 12px;
    padding: 0.7rem 1rem;
    font-size: 0.92rem;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
}

.request-btn--accept {
    background: linear-gradient(135deg, #8b5cf6, #6d28d9);
    color: white;
    box-shadow: 0 6px 16px rgba(109, 40, 217, 0.22);
}

.request-btn--accept:hover {
    transform: translateY(-1px);
    box-shadow: 0 10px 22px rgba(109, 40, 217, 0.28);
}

.request-btn--decline {
    background: #f5f3ff;
    color: #6d28d9;
    border: 1px solid #ddd6fe;
}

.request-btn--decline:hover {
    background: #ede9fe;
}

.empty-state {
    margin-top: 2rem;
    padding: 2.25rem 1.5rem;
    text-align: center;
    background: #ffffff;
    border: 1px solid #ede9fe;
    border-radius: 20px;
}

.empty-title {
    margin: 0;
    font-size: 1.15rem;
    font-weight: 700;
    color: #1f2937;
}

.empty-subtitle {
    margin: 0.5rem 0 0;
    font-size: 0.95rem;
    color: #6b7280;
}

@media (max-width: 640px) {
    .request-card {
        flex-direction: column;
        align-items: stretch;
    }

    .request-actions {
        width: 100%;
    }

    .request-btn {
        flex: 1;
    }
}


</style>
