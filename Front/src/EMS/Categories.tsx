import {api, jsonHeaders, queryParams, usePaged} from "../api.ts";
import type {CategoryDTO} from "../models.ts";
import {useState} from "react";
import {Input, Section} from "../Components/SmallComponents.tsx";
import {Button} from "../Components/Button.tsx";
import {Pagination} from "../Components/Pagination.tsx";

export function EMSCategories() {
    const {data, loading, error, page, setPage, size} = usePaged<CategoryDTO>(
        ({page, size}) => api(`categories${queryParams({page, size})}`),
        []
    );
    const [name, setName] = useState("");
    const [desc, setDesc] = useState("");

    async function add() {
        try {
            await api("categories", {
                method: "POST",
                headers: jsonHeaders,
                body: JSON.stringify({name, description: desc}),
            });
            window.location.reload();
        } catch (e: any) {
            alert(e?.message || String(e));
        }
    }

    async function update(c: CategoryDTO) {
        const newName = window.prompt("Novi naziv", c.name || (c as any).naziv);
        const newDesc = window.prompt("Novi opis", c.description || (c as any).opis);
        if (newName == null || newDesc == null) return;
        try {
            await api(`categories/${c.id}`, {
                method: "PUT",
                headers: jsonHeaders,
                body: JSON.stringify({name: newName, description: newDesc}),
            });
            window.location.reload();
        } catch (e: any) {
            alert(e?.message || String(e));
        }
    }

    async function del(c: CategoryDTO) {
        if (!window.confirm("Obrisati kategoriju?")) return;
        try {
            await api(`categories/${c.id}`, {method: "DELETE"});
            window.location.reload();
        } catch (e: any) {
            alert(e?.message || String(e));
        }
    }

    return (
        <Section
            title="Kategorije"
            right={
                <div className="flex flex-wrap gap-2 items-end">
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                        <Input label="Naziv" value={name} onChange={(e) => setName(e.target.value)}/>
                        <Input label="Opis" value={desc} onChange={(e) => setDesc(e.target.value)}/>
                    </div>
                    <Button variant="primary" onClick={add}>Dodaj</Button>
                </div>
            }
        >
            {loading && <div>Učitavanje…</div>}
            {error && <div className="text-red-600">{error}</div>}
            <div
                className="overflow-x-auto -mx-2 md:mx-0 min-w-full rounded-xl border border-gray-200 bg-white shadow-sm">
                <table className="w-full text-sm text-gray-900">
                    <thead className="bg-gray-50 text-gray-700">
                    <tr className="[&>th]:py-3 [&>th]:px-3 border-b border-gray-200">
                        <th className="py-2">Naziv</th>
                        <th>Opis</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-200">
                    {(data.items || []).map((c) => (
                        <tr key={c.id} className="border-b hover:bg-gray-50">
                            <td className="py-3 px-3">{c.name}</td>
                            <td className="px-3">{c.description}</td>
                            <td className="px-3 text-right space-x-2">
                                <Button size="sm" variant="primary" onClick={() => update(c)}>Izmena</Button>
                                <Button size="sm" variant="danger" onClick={() => del(c)}>Brisanje</Button>
                            </td>
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