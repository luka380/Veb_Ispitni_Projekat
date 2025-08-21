import type {CategoryDTO} from "../models.ts";
import {useState} from "react";
import {Pill, Section} from "../Components/SmallComponents.tsx";
import {CategoryEvents} from "./CategoryEvents.tsx";

export function CategoriesView({categories}: { categories: CategoryDTO[] }) {
    const [selected, setSelected] = useState<CategoryDTO | null>(null);
    return (
        <div className="grid grid-cols-1 lg:grid-cols-12 gap-6">
            <div className="lg:col-span-4">
                <Section title="Kategorije">
                    <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-2 gap-2">
                        {(categories || []).map((c) => (
                            <Pill key={c.id} active={selected?.id === c.id} onClick={() => setSelected(c)}>
                                {c.name || (c as any).title}
                            </Pill>
                        ))}
                    </div>
                </Section>
            </div>
            <div className="lg:col-span-8">
                {selected ? (
                    <CategoryEvents category={selected}/>
                ) : (
                    <Section title="Izaberite kategoriju">—</Section>
                )}
            </div>
        </div>
    );
}