import React from "react";

export function Button(
    props: React.ButtonHTMLAttributes<HTMLButtonElement> & {
        variant?: "primary" | "ghost" | "soft" | "danger";
        size?: "sm" | "md";
    }
) {
    const {className = "", variant = "soft", size = "md", ...rest} = props;
    const base =
        "inline-flex items-center justify-center whitespace-nowrap rounded-xl font-medium transition disabled:opacity-50 disabled:cursor-not-allowed";
    const paddings = size === "sm" ? "px-3 py-1.5 text-sm" : "px-4 py-2";
    const variants = {
        primary: "bg-black text-white hover:bg-black/90",
        soft: "bg-gray-100 text-white hover:bg-gray-200",
        ghost: "bg-transparent hover:bg-gray-100",
        danger: "bg-red-600 text-white hover:bg-red-600/90",
    } as const;
    return <button className={`${base} ${paddings} ${variants[variant]} ${className}`} {...rest} />;
}