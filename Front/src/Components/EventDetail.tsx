import {useEffect, useState} from "react";
import type {CommentDTO, EventDTO, PageDTO, TagDTO} from "../models.ts";
import {api, BASE, formatToDT, jsonHeaders, normalizePage} from "../api.ts";
import {Input, Section} from "./SmallComponents.tsx";
import {Button} from "./Button.tsx";
import {Tag} from "./EventTag.tsx";
import {SimilarEvents} from "./EventSimilar.tsx";
import {useApp} from "../AppContext.tsx";

export function EventDetail({eventId}: { eventId: number }) {
    const {state} = useApp()
    const [event, setEvent] = useState<EventDTO | null>(null);
    const [err, setErr] = useState("");
    const [commentText, setCommentText] = useState("");
    const [commentName, setCommentName] = useState(state.currentAccount?.firstName != null ? state.currentAccount.firstName : "");
    const [comments, setComments] = useState<CommentDTO[]>([]);
    const [rsvpStr, setRSVPStr] = useState("");

    async function load() {
        try {
            setRSVPStr(await api("events/" + eventId + "/rsvpString", {asText: true}));
            const e = await api<EventDTO>(`events/${eventId}`);
            setEvent(e);
            if (e.userStatus.reaction.reaction === "NO_VIEW") {
                await react("NO_REACTION");
            }

            const cs = await api<PageDTO<CommentDTO> | CommentDTO[]>(
                `events/${eventId}/comments?page=0&size=10`
            );
            setComments(normalizePage<CommentDTO>(cs).items);
        } catch (e: any) {
            setErr(e?.message || String(e));
        }
    }

    useEffect(() => {
        load();
    }, [eventId]);

    useEffect(() => {
        function handler(ev: Event) {
            if (ev instanceof CustomEvent && ev.detail === eventId) {
                load();
            }
        }

        window.addEventListener("refresh-event", handler as EventListener);
        return () => window.removeEventListener("refresh-event", handler as EventListener);
    }, [eventId]);

    async function react(kind: "NO_VIEW" | "NO_REACTION" | "LIKE" | "DISLIKE") {
        try {
            console.log(kind + "REACTION")
            await fetch(BASE + `events/${eventId}/react`, {
                method: "POST",
                credentials: "include",
                headers: jsonHeaders,
                body: JSON.stringify({reaction: kind}).toString(),
            });
            if (kind !== "NO_REACTION") {
                load();
            }
        } catch (e: any) {
            alert(e?.message || String(e));
        }
    }

    async function rsvp() {
        const email = window.prompt("Vaš email za RSVP?") || "";
        if (!email) return;
        const res = await fetch(BASE + `events/${eventId}/rsvp`, {
            method: "POST",
            credentials: "include",
            headers: jsonHeaders,
            body: JSON.stringify({email: email}).toString(),
        });
        if (res.ok)
            alert("Prijava poslata.");
        else
            alert("Prijava nije uspela.");
    }

    async function addComment() {
        try {
            const body = JSON.stringify({authorName: commentName, text: commentText});
            await api(`events/${eventId}/comments`, {method: "POST", headers: jsonHeaders, body});
            setCommentName(state.currentAccount?.firstName != null ? state.currentAccount.firstName : "");
            setCommentText("");
            window.dispatchEvent(new CustomEvent<number>("refresh-event", {detail: eventId}));
        } catch (e: any) {
            alert(e?.message || String(e));
        }
    }

    if (err) return <div className="text-red-600">{err}</div>;
    if (!event) return <div>Učitavanje…</div>;

    const tags = (event.tags ?? []) as (TagDTO | string)[];
    const likes = event.eventStatus.likes ?? 0;
    const dislikes = event.eventStatus.dislikes ?? 0;

    return (
        <Section className="text-gray-800"
                 title={event.title}
                 right={<span className="text-sm text-gray-500">Pregleda: {event.eventStatus.views ?? 0}</span>}
        >
            <div className="prose max-w-none prose-p:my-0 text-gray-800 whitespace-pre-line mb-3">
                {event.description}
            </div>
            <div className="text-sm text-gray-700 grid sm:grid-cols-2 gap-2 mb-4">
                <div>
                    <b>Vreme održavanja:</b> {formatToDT(event.startsAt)}
                </div>
                <div>
                    <b>Lokacija:</b> {event.location}
                </div>
                <div>
                    <b>Kreirano:</b> {formatToDT(event.createdAt)}
                </div>
                <div>
                    <b>Autor:</b> {event.author?.firstName + " " + event.author?.lastName}
                </div>
                <div>
                    <b>Kategorija:</b> {(event.category && (event.category.name)) || "—"}
                </div>
                <div className="flex flex-wrap items-center gap-2">
                    <b>Tagovi:</b>
                    {tags.map((t, i) => (
                        <Tag
                            key={(t as any).id ?? (typeof t === "string" ? `${t}-${i}` : i)}
                            value={typeof t === "string" ? t : t.name || (t as any).naziv || ""}
                        />
                    ))}
                </div>
            </div>

            <div className="flex items-center gap-3 mb-4">
                <Button variant="soft" onClick={() => react("LIKE")}>👍 {likes}</Button>
                <Button variant="soft" onClick={() => react("DISLIKE")}>👎 {dislikes}</Button>
                {event.maxCapacity && event.maxCapacity > 0 && (
                    <Button className="ml-2" variant="primary" onClick={rsvp}>RSVP {rsvpStr}</Button>
                )}
            </div>

            <SimilarEvents id={eventId}/>

            <div className="mt-6">
                <h3 className="font-semibold mb-3">Komentari</h3>
                <div className="space-y-2 mb-3">
                    {(comments || []).map((c) => (
                        <div key={c.id} className="p-3 border rounded-2xl">
                            <div className="text-sm">
                                <b>{c.authorName}</b>{" "}
                                <span className="text-gray-500">{formatToDT(c.createdAt)}</span>
                            </div>
                            <div className="text-gray-800">{c.text}</div>
                            <div className="text-xs text-gray-500">👍 {c.likes ?? 0} · 👎 {c.dislikes ?? 0}</div>
                        </div>
                    ))}
                    {(!comments || comments.length === 0) && (
                        <div className="text-sm text-gray-500">Nema komentara.</div>
                    )}
                </div>
                <div className="grid gap-2 sm:grid-cols-[1fr_3fr_auto]">
                    <Input placeholder="Vaše ime" value={commentName} onChange={(e) => setCommentName(e.target.value)}/>
                    <Input placeholder="Vaš komentar" value={commentText}
                           onChange={(e) => setCommentText(e.target.value)}/>
                    <Button onClick={addComment}>Dodaj</Button>
                </div>
            </div>
        </Section>
    );
}