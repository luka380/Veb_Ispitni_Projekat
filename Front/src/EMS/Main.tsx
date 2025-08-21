import {useState} from "react";
import {Pill} from "../Components/SmallComponents.tsx";
import {EMSEvents} from "./Events.tsx";
import {EMSCategories} from "./Categories.tsx";
import {EMSSearch} from "./Search.tsx";
import {EMSUsers} from "./Users.tsx";
import {UserType} from "../models.ts";

export function EMS({role}: { role: UserType }) {
    const [tab, setTab] = useState<"events" | "categories" | "search" | "users">("events");

    return (
        <div className="grid gap-6 text-gray-700">
            <div className="flex flex-wrap gap-2">
                {(
                    [
                        ["events", "Događaji"],
                        ["categories", "Kategorije"],
                        ...(role === UserType.ADMIN ? ([["users", "Korisnici"]] as const) : []),
                        ["search", "Pretraga događaja"],
                    ] as const
                ).map(([id, label]) => (
                    <Pill key={id} active={tab === id} onClick={() => setTab(id as any)}>
                        {label}
                    </Pill>
                ))}
            </div>

            {tab === "events" && <EMSEvents/>}
            {tab === "categories" && <EMSCategories/>}
            {tab === "search" && <EMSSearch/>}
            {tab === "users" && role === UserType.ADMIN && <EMSUsers/>}
        </div>
    );
}