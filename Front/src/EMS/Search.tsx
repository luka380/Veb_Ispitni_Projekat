import {useState} from "react";
import {api, formatToDT, queryParams, usePaged} from "../api.ts";
import type {EventDTO} from "../models.ts";
import {Input, Section} from "../Components/SmallComponents.tsx";
import {Pagination} from "../Components/Pagination.tsx";

export function EMSSearch() {
    const [query, setQuery] = useState("");
    const {data, loading, error, page, setPage, size} = usePaged<EventDTO>(
        ({page, size}) => api(`events/search${queryParams({search: query, page, size})}`),
        [query]
    );
    return (
        <Section title="Pretraga događaja (EMS)">
            <div className="mb-3 max-w-xl">
                <Input placeholder="Naslov ili opis" value={query} onChange={(e) => setQuery(e.target.value)}/>
            </div>
            {loading && <div>Učitavanje…</div>}
            {error && <div className="text-red-600">{error}</div>}
            <div className="overflow-x-auto -mx-2 md:mx-0 text-gray-900">
                <table className="w-full text-sm">
                    <thead>
                    <tr className="text-left border-b">
                        <th className="py-2">Naslov</th>
                        <th>Autor</th>
                        <th>Kreirano</th>
                    </tr>
                    </thead>
                    <tbody>
                    {(data.items || []).map((e) => (
                        <tr key={e.id} className="border-b">
                            <td className="py-2">{e.title}</td>
                            <td>{e.author?.firstName + " " + e.author?.lastName}</td>
                            <td>{formatToDT(e.createdAt)}</td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
            <Pagination page={page} size={size} total={data.total} onPrev={() => setPage(Math.max(0, page - 1))}
                        onNext={() => setPage(page + 1)}/>
        </Section>
    );
}