import {useEffect, useState} from "react";
import type {EventDTO} from "../models.ts";
import {api} from "../api.ts";

export function SimilarEvents({id}: { id: number }) {
    const [items, setItems] = useState<EventDTO[]>([]);
    useEffect(() => {
        api<EventDTO[]>(`events/${id}/similar`).then(setItems).catch(() => {
        });
    }, [id]);
    if (!items?.length) return null;
    return (
        <div className="mt-4">
            <div className="font-semibold mb-2 text-gray-900">Pročitaj još…</div>
            <ul className="list-disc list-inside text-sm space-y-1">
                {items.slice(0, 3).map((e) => (
                    <li key={e.id}>
                        <a
                            href="#"
                            onClick={(ev) => {
                                ev.preventDefault();
                                window.dispatchEvent(new CustomEvent<number>("open-event", {detail: e.id}));
                            }}
                            className="hover:underline"
                        >
                            {e.title}
                        </a>
                    </li>
                ))}
            </ul>
        </div>
    );
}