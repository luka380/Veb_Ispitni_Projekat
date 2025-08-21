export function Tag({value}: { value: string }) {
    const label = value;
    return (
        <button
            className="px-2 py-0.5 rounded-full bg-gray-100 text-xs hover:bg-gray-200"
            onClick={() => -window.dispatchEvent(new CustomEvent("public-search", {detail: "#" + value}))}
        >
            {label}
        </button>
    );
}