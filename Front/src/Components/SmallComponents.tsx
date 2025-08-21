import React, {useMemo} from "react";

type SectionProps = {
    title: React.ReactNode;
    right?: React.ReactNode;
    children?: React.ReactNode;
    className?: string;
};

export function Section({title, right, children, className = ""}: SectionProps) {
    return (
        <section className={`p-5 bg-white rounded-2xl shadow-sm ring-1 ring-gray-100 ${className}`}>
            <div className="text-gray-700 flex flex-wrap items-center gap-3 justify-between mb-4">
                <h2 className="text-xl text-gray-900 md:text-2xl font-semibold tracking-tight">{title}</h2>
                {right}
            </div>
            {children}
        </section>
    );
}

export function Input(
    props: React.InputHTMLAttributes<HTMLInputElement> & { label?: string; hint?: string }
) {
    const {label, hint, className = "", id, ...rest} = props;
    const inputId = useMemo(() => id || `in_${Math.random().toString(36).slice(2, 9)}`, [id]);
    return (
        <label className="grid gap-1 text-sm text-gray-700">
            {label && <span className="font-medium">{label}</span>}
            <input
                id={inputId}
                className={`w-full h-11 rounded-xl border border-gray-200 bg-white px-3 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-black/20 ${className}`}
                {...rest}
            />
            {hint && <span className="text-xs text-gray-500">{hint}</span>}
        </label>
    );
}

export function Textarea(
    props: React.TextareaHTMLAttributes<HTMLTextAreaElement> & { label?: string; hint?: string }
) {
    const {label, hint, className = "", ...rest} = props;
    return (
        <label className="grid gap-1 text-sm text-gray-700">
            {label && <span className="font-medium">{label}</span>}
            <textarea
                className={`w-full min-h-[120px] rounded-xl border border-gray-200 bg-white px-3 py-2 placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-black/20 ${className}`}
                {...rest}
            />
            {hint && <span className="text-xs text-gray-500">{hint}</span>}
        </label>
    );
}

export function Pill({active, children, onClick}: {
    active?: boolean;
    children: React.ReactNode;
    onClick?: () => void
}) {
    return (
        <button
            onClick={onClick}
            className={`px-3 py-1 rounded-2xl border transition ${
                active ? "bg-black text-white border-black" : "bg-white border-gray-200 hover:bg-gray-50"
            }`}
        >
            {children}
        </button>
    );
}