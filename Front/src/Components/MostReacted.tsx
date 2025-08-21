import {useEffect, useState} from "react";
import type {EventDTO} from "../models.ts";
import {api} from "../api.ts";

export function MostReactedBlock() {
    const [items, setItems] = useState<EventDTO[]>([]);
    useEffect(() => {
        api<EventDTO[]>("events/most-reacted").then(setItems).catch(() => {
        });
    }, []);
    if (!items?.length) return <p className="text-sm text-gray-600">Nema rezultata.</p>;
    return (
        <div
            className="hidden md:block fixed bottom-5 right-5 w-[22rem] p-4 bg-white rounded-2xl shadow-xl ring-1 ring-gray-100">
            <div className="text-sm font-semibold mb-2">Najviše reakcija</div>
            <ul className="space-y-2">
                {items.slice(0, 3).map((e) => (
                    <li key={e.id} className="text-sm">
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