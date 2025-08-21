import type {EventDTO} from "../models.ts";
import {useState} from "react";
import {formatToDT} from "../api.ts";
import {EventDetail} from "./EventDetail.tsx";

export function EventList({items = [] as EventDTO[]}) {
    const [selected, setSelected] = useState<EventDTO | null>(null);
    return (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            <div className="lg:col-span-5 space-y-3">
                {(items || []).slice(0, 50).map((e) => (
                    <div
                        key={e.id}
                        className={`p-4 border rounded-2xl cursor-pointer transition hover:bg-gray-50 ${
                            selected?.id === e.id ? "ring-2 ring-black/10" : ""
                        }`}
                        onClick={() => setSelected(e)}
                    >
                        <div className="font-semibold text-gray-900">{e.title}</div>
                        <div className="text-sm text-gray-700 line-clamp-2">{e.description}</div>
                        <div className="text-xs text-gray-500">Objavljeno: {formatToDT(e.createdAt)}</div>
                    </div>
                ))}
                {(items || []).length === 0 && (
                    <div className="text-sm text-gray-600">Nema rezultata.</div>
                )}
            </div>
            <div className="lg:col-span-7">
                {selected ? (
                    <EventDetail eventId={selected.id}/>
                ) : (
                    <div className="p-6 text-gray-500">Klik na događaj za detalje →</div>
                )}
            </div>
        </div>
    );
}