import type {CategoryDTO, EventDTO} from "../models.ts";
import {api, queryParams, usePaged} from "../api.ts";
import {Section} from "../Components/SmallComponents.tsx";
import {EventGrid} from "../Components/EventGrid.tsx";
import {Pagination} from "../Components/Pagination.tsx";

export function CategoryEvents({category}: { category: CategoryDTO }) {
    const {data, loading, error, page, setPage, size} = usePaged<EventDTO>(
        ({page, size}) => api(`categories/${category.id}${queryParams({page, size})}`),
        [category.id]
    );
    return (
        <Section title={`Događaji — ${category.name}`}>
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