export const UserType = {
    ANONYMOUS: "ANONYMOUS",
    EVENT_CREATOR: "EVENT_CREATOR",
    ADMIN: "ADMIN",
} as const;

export type UserType = typeof UserType[keyof typeof UserType];
export type UserStatus = "ACTIVE" | "INACTIVE" | "BLOCKED" | string;

export interface UserDTO {
    id: number;
    firstName?: string;
    lastName?: string;
    fullName?: string;
    email?: string;
    userType: UserType;
    userStatus: UserStatus;
}

export interface CategoryDTO {
    id: number;
    name?: string;
    description?: string;
}

export interface TagDTO {
    id?: number;
    name?: string;
}

export interface EventDTO {
    id: number;
    title?: string;
    description?: string;
    createdAt?: string;
    startsAt?: string;
    location?: string;
    views?: number;
    likeCount?: number;
    dislikeCount?: number;
    maxCapacity?: number | null;
    author?: UserDTO;
    category?: CategoryDTO;
    tags?: (TagDTO | string)[];
    eventStatus: {
        eventId: number;
        likes: number;
        dislikes: number;
        views: number;
    };
    userStatus: {
        userId: number;
        eventId: number;
        reaction: "NO_VIEW" | "NO_REACTION" | "LIKE" | "DISLIKE"
    };
}

export interface CommentDTO {
    id: number;
    authorName?: string;
    text?: string;
    createdAt?: string;
    likes?: number;
    dislikes?: number;
}

export interface PageDTO<T> {
    items?: T[];
    total?: number;
    page?: number;
    size?: number;
    content?: T[];
    results?: T[];
    data?: T[];
    totalElements?: number;
    count?: number;
}