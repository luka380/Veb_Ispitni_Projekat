import {useEffect, useState} from "react";
import type {PageDTO, UserDTO} from "./models.ts";

export const BASE = "http://localhost:8080/Ispitni_Projekat_F-1.0-SNAPSHOT/" as const;

export const jsonHeaders: HeadersInit = {"Content-Type": "application/json"};


export function queryParams(params?: Record<string, string | number | boolean | null | undefined>): string {
    const sp = new URLSearchParams();
    Object.entries(params || {}).forEach(([k, v]) => {
        if (v === undefined || v === null || v === "") return;
        sp.append(k, String(v));
    });
    const s = sp.toString();
    return s ? `?${s}` : "";
}

export function formatToDT(value?: string | null): string {
    if (!value) return "";
    try {
        const d = new Date(value);
        return d.toLocaleString();
    } catch {
        return String(value);
    }
}

export function normalizePage<T>(d: unknown): { items: T[]; total: number; page?: number; size?: number } {
    if (Array.isArray(d)) return {items: d as T[], total: (d as T[]).length};
    if (d && typeof d === "object") {
        const obj = d as PageDTO<T>;
        const items = obj.items ?? obj.content ?? obj.results ?? obj.data ?? [];
        const total = obj.total ?? obj.totalElements ?? obj.count ?? items.length;
        return {items, total, page: obj.page, size: obj.size};
    }
    return {items: [], total: 0};
}

export async function api<T = unknown>(
    path: string,
    opts: { method?: string; body?: BodyInit | null; headers?: HeadersInit; asText?: boolean } = {}
): Promise<T extends string ? string : any> {
    const {method = "GET", body = null, headers = {}, asText = false} = opts;
    const hdrs: HeadersInit = {...(headers || {})};
    const res = await fetch(BASE + path.replace(/^\//, ""), {
        method,
        credentials: "include",
        headers: hdrs,
        body,
    });
    if (!res.ok) {
        const msg = await res.text().catch(() => "");
        throw new Error(msg || `${res.status} ${res.statusText}`);
    }
    if (asText) return (await res.text()) as any;
    if (res.status === 204) return null as any;
    const ct = res.headers.get("content-type") || "";
    if (ct.includes("application/json")) return (await res.json()) as any;
    return (await res.text()) as any;
}

export function usePaged<T>(fetcher: (p: { page: number; size: number }) => Promise<unknown>, deps: any[]) {
    const [page, setPage] = useState<number>(0);
    const [size, setSize] = useState<number>(10);
    const [data, setData] = useState<{ items: T[]; total: number }>({items: [], total: 0});
    const [loading, setLoading] = useState<boolean>(false);
    const [error, setError] = useState<string>("");

    useEffect(() => {
        let stop = false;
        setLoading(true);
        setError("");
        fetcher({page, size})
            .then((d) => !stop && setData(normalizePage<T>(d)))
            .catch((e: any) => !stop && setError(e?.message || String(e)))
            .finally(() => !stop && setLoading(false));
        return () => {
            stop = true;
        };
    }, [page, size, ...deps]);

    return {page, setPage, size, setSize, data, setData, loading, error} as const;
}

export async function getUserInfo(): Promise<UserDTO | null> {
    try {
        return await api<UserDTO>("auth");
    } catch {
        return null;
    }
}
