import {api, formatToDT, jsonHeaders, normalizePage, queryParams, usePaged} from "../api.ts";
import type {CategoryDTO, EventDTO, PageDTO} from "../models.ts";
import {useEffect, useState} from "react";
import {Input, Section, Textarea} from "../Components/SmallComponents.tsx";
import {Button} from "../Components/Button.tsx";
import {Pagination} from "../Components/Pagination.tsx";

export function EMSEvents() {
    const {data, loading, error, page, setPage, size} = usePaged<EventDTO>(
        ({page, size}) => api(`events${queryParams({page, size})}`),
        []
    );
    const [showForm, setShowForm] = useState(false);
    const [editing, setEditing] = useState<EventDTO | null>(null);

    return (
        <Section
            title="Događaji"
            right={
                <Button variant="primary" onClick={() => {
                    setEditing(null);
                    setShowForm(true);
                }}>
                    Dodaj
                </Button>
            }
        >
            {loading && <div>Učitavanje…</div>}
            {error && <div className="text-red-600">{error}</div>}

            <div className="overflow-x-auto -mx-2 md:mx-0">
                <table className="w-full text-sm">
                    <thead>
                    <tr className="text-left border-b">
                        <th className="py-2">Naslov</th>
                        <th>Autor</th>
                        <th>Datum kreiranja</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    {(data.items || []).map((e) => (
                        <tr key={e.id} className="border-b">
                            <td className="py-2">
                                <a
                                    href="#"
                                    onClick={(ev) => {
                                        ev.preventDefault();
                                        window.dispatchEvent(new CustomEvent<number>("open-event", {detail: e.id}));
                                    }}
                                    className="hover:underline"
                                >
                                    {e.title}
                                </a>
                            </td>
                            <td>{e.author?.firstName + " " + e.author?.lastName}</td>
                            <td>{formatToDT(e.createdAt)}</td>
                            <td className="text-right space-x-2">
                                <Button size="sm" onClick={() => {
                                    setEditing(e);
                                    setShowForm(true);
                                }}>Izmena</Button>
                                <Button size="sm" variant="danger" onClick={() => delEvent(e.id)}>Brisanje</Button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>

            <Pagination page={page} size={size} total={data.total} onPrev={() => setPage(Math.max(0, page - 1))}
                        onNext={() => setPage(page + 1)}/>

            {showForm && (
                <EventForm
                    onClose={() => setShowForm(false)}
                    editing={editing}
                    onSaved={() => window.location.reload()}
                />
            )}
        </Section>
    );
}

async function delEvent(id: number) {
    if (!window.confirm("Obrisati događaj i sve komentare?")) return;
    try {
        await api(`events/${id}`, {method: "DELETE"});
        window.location.reload();
    } catch (e: any) {
        alert(e?.message || String(e));
    }
}

function EventForm({editing, onClose, onSaved}: {
    editing: EventDTO | null;
    onClose: () => void;
    onSaved?: () => void
}) {
    const [title, setTitle] = useState<string>(editing?.title || "");
    const [description, setDescription] = useState<string>(editing?.description || "");
    const [startsAt, setStartsAt] = useState<string>(editing?.startsAt || "");
    const [location, setLocation] = useState<string>(editing?.location || "");
    const [categoryId, setCategoryId] = useState<string | number>(editing?.category?.id || "");
    const [tags, setTags] = useState<string>((editing?.tags || []).map((t: any) => t?.name || t).join(", "));
    const [maxCapacity, setMaxCapacity] = useState<string | number | null>((editing?.maxCapacity ?? "") as any);
    const [categories, setCategories] = useState<CategoryDTO[]>([]);
    const [err, setErr] = useState("");

    useEffect(() => {
        api<PageDTO<CategoryDTO> | CategoryDTO[]>("categories?page=0&size=100")
            .then((d) => setCategories(normalizePage<CategoryDTO>(d).items))
            .catch(() => {
            });
    }, []);

    async function save() {
        setErr("");
        const payload = {
            title,
            description,
            startsAt,
            location,
            categoryId: Number(categoryId),
            tags: (tags || "")
                .split(/[,;|]+/)
                .map((s) => s.trim())
                .filter(Boolean),
            maxCapacity: maxCapacity === "" ? null : Number(maxCapacity),
        } as const;
        try {
            if (editing?.id) {
                await api(`events/${editing.id}`, {
                    method: "PUT",
                    headers: jsonHeaders,
                    body: JSON.stringify(payload),
                });
            } else {
                await api("events", {
                    method: "POST",
                    headers: jsonHeaders,
                    body: JSON.stringify(payload),
                });
            }
            onSaved?.();
            onClose?.();
        } catch (e: any) {
            setErr(e?.message || String(e));
        }
    }

    return (
        <div
            className="fixed inset-0 bg-black/40 z-50 overflow-y-auto p-4 md:p-6 min-h-[100svh] flex items-start md:items-center justify-center">
            <div
                className="bg-white rounded-2xl shadow-xl w-full max-w-3xl p-5 ring-1 ring-gray-100 mx-auto my-8 max-h-[92svh] md:max-h-[90vh] overflow-y-auto">
                <div className="flex items-center justify-between mb-4">
                    <div className="text-lg font-semibold">{editing ? "Izmena događaja" : "Novi događaj"}</div>
                    <Button variant="ghost" onClick={onClose}>Zatvori</Button>
                </div>
                {err && <div
                    className="mb-3 rounded-xl border border-red-200 bg-red-50 p-2 text-sm text-red-700">{err}</div>}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                    <Input label="Naslov" placeholder="Naslov događaja" value={title}
                           onChange={(e) => setTitle(e.target.value)} className="md:col-span-2"/>
                    <Textarea label="Opis" placeholder="Kratak opis" value={description}
                              onChange={(e) => setDescription(e.target.value)} className="md:col-span-2"/>
                    <Input label="Datum i vreme (ISO)" placeholder="2025-08-14T18:30:00" value={startsAt}
                           onChange={(e) => setStartsAt(e.target.value)}/>
                    <Input label="Lokacija" placeholder="Npr. Zgrada A, sala 201" value={location}
                           onChange={(e) => setLocation(e.target.value)}/>
                    <label className="grid gap-1 text-sm text-gray-700">
                        <span className="font-medium">Kategorija</span>
                        <select
                            className="w-full h-11 rounded-xl border border-gray-200 bg-white px-3 focus:outline-none focus:ring-2 focus:ring-black/20"
                            value={categoryId}
                            onChange={(e) => setCategoryId((e.target as HTMLSelectElement).value)}
                        >
                            <option value="">— Kategorija —</option>
                            {(categories || []).map((c) => (
                                <option key={c.id} value={c.id}>
                                    {c.name || (c as any).naziv}
                                </option>
                            ))}
                        </select>
                    </label>
                    <Input label="Tagovi (zarezima)" placeholder="javascript, meetup" value={tags}
                           onChange={(e) => setTags(e.target.value)}/>
                    <Input label="Max kapacitet (opciono)" placeholder="50" value={String(maxCapacity ?? "")}
                           onChange={(e) => setMaxCapacity(e.target.value)}/>
                </div>
                <div className="mt-4 flex items-center gap-2">
                    <Button variant="primary" onClick={save}>Sačuvaj</Button>
                </div>
            </div>
        </div>
    );
}