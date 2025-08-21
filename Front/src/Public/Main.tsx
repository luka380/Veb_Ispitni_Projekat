import {useEffect, useState} from "react";
import type {CategoryDTO} from "../models.ts";
import {api} from "../api.ts";
import {Pill} from "../Components/SmallComponents.tsx";
import {MostReactedBlock} from "../Components/MostReacted.tsx";
import {HomeLatest} from "./HomeLatest.tsx";
import {TopViewed} from "./TopViewed.tsx";
import {CategoriesView} from "./CategoriesView.tsx";
import {SearchView} from "./SearchView.tsx";

export function PublicArea() {
    const [tab, setTab] = useState<"home" | "top" | "categories" | "search">("home");
    const [search, setSearch] = useState("");
    const [categories, setCategories] = useState<CategoryDTO[]>([]);

    useEffect(() => {
        api<CategoryDTO[]>("categories").then(setCategories).catch(() => {
        });

        function onPublicSearch(ev: Event) {
            if (ev instanceof CustomEvent && typeof ev.detail === "string") {
                setSearch(ev.detail);
                setTab("search");
            }
        }

        window.addEventListener("public-search", onPublicSearch as EventListener);
        return () => window.removeEventListener("public-search", onPublicSearch as EventListener);

    }, []);

    return (
        <div className="grid gap-6 text-gray-700">
            <div className="flex flex-wrap items-center gap-2">
                {[
                    ["home", "Početna"],
                    ["top", "Najposećeniji"],
                    ["categories", "Kategorije"],
                    ["search", "Pretraga"],
                ].map(([id, label]) => (
                    <Pill key={id} active={tab === (id as any)} onClick={() => setTab(id as any)}>
                        {label}
                    </Pill>
                ))}
            </div>

            {tab === "home" && <HomeLatest/>}
            {tab === "top" && <TopViewed/>}
            {tab === "categories" && <CategoriesView categories={categories}/>}
            {tab === "search" && <SearchView queryProp={search}/>}

            <MostReactedBlock/>
        </div>
    );
}