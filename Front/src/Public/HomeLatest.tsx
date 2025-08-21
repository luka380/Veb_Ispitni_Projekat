import {api, queryParams, usePaged} from "../api.ts";
import type {EventDTO} from "../models.ts";
import {Section} from "../Components/SmallComponents.tsx";
import {EventList} from "../Components/EventList.tsx";

export function HomeLatest() {
    const {data, loading, error} = usePaged<EventDTO>(({page, size}) => api(`events${queryParams({page, size})}`), []);
    return (
        <Section title="Najnoviji događaji">
            {loading && <div>Učitavanje…</div>}
            {error && <div className="text-red-600">{error}</div>}
            <EventList items={data.items}/>
        </Section>
    );
}