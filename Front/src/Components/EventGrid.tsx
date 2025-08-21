import type {EventDTO} from "../models.ts";
import {formatToDT} from "../api.ts";

export function EventGrid({items = [] as EventDTO[]}) {
    if (!items?.length) return <div className="text-sm text-gray-600">Nema rezultata.</div>;
    return (
        <div
            className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 2xl:grid-cols-6 gap-4">
            {items.slice(0, 50).map((e) => (
                <article
                    key={e.id}
                    className="group rounded-2xl border border-gray-200 bg-white p-4 shadow-sm transition hover:shadow-md cursor-pointer"
                    onClick={() => window.dispatchEvent(new CustomEvent<number>("open-event", {detail: e.id}))}
                >
                    <div className="mb-1 flex items-center justify-between gap-3">
                        <h3 className="text-base font-semibold leading-tight group-hover:underline">
                            {e.title}
                        </h3>
                        <span className="text-xs text-gray-500">{formatToDT(e.createdAt)}</span>
                    </div>
                    <p className="line-clamp-3 text-sm text-gray-700">{e.description}</p>
                    <div className="mt-3 text-xs text-gray-500">Lokacija: {e.location || "—"}</div>
                </article>
            ))}
        </div>
    );
}