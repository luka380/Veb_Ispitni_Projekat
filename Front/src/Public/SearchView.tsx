import {useState} from "react";
import {api, queryParams, usePaged} from "../api.ts";
import type {EventDTO} from "../models.ts";
import {Input, Section} from "../Components/SmallComponents.tsx";
import {EventGrid} from "../Components/EventGrid.tsx";
import {Pagination} from "../Components/Pagination.tsx";

export function SearchView({queryProp}: { queryProp?: string }) {
    const [query, setQuery] = useState<string>(queryProp || "");
    const {data, loading, error, page, setPage, size} = usePaged<EventDTO>(
        ({page, size}) => {
            if (query.toString().startsWith("#"))
                return api(`events/search${queryParams({page, size, tag: query.replace("#", "")})}`)
            else
                return api(`events/search${queryParams({page, size, search: query})}`)
        },
        [query]
    );
    return (
        <Section title="Pretraga">
            <div className="mb-4 max-w-xl">
                <Input placeholder="Naslov ili opis" value={query} onChange={(e) => setQuery(e.target.value)}/>
            </div>
            {loading && <div>Učitavanje…</div>}
            {error && <div className="text-red-600">{error}</div>}
            <EventGrid items={data.items}/>
            <Pagination
                page={page}
                size={size}
                total={data.total}
                onPrev={() => setPage(Math.max(0, page - 1))}
                onNext={() => setPage(page + 1)}
            />
        </Section>
    );
}