import {useEffect, useState} from "react";
import type {EventDTO} from "../models.ts";
import {api} from "../api.ts";
import {Section} from "../Components/SmallComponents.tsx";
import {EventGrid} from "../Components/EventGrid.tsx";

export function TopViewed() {
    const [items, setItems] = useState<EventDTO[]>([]);
    const [err, setErr] = useState("");
    useEffect(() => {
        api<EventDTO[]>("events/top?days=30").then(setItems).catch((e: any) => setErr(e?.message || String(e)));
    }, []);
    return (
        <Section title="Top 10 (posl. 30 dana)">
            {err && <div className="text-red-600">{err}</div>}
            <EventGrid items={items}/>
        </Section>
    );
}