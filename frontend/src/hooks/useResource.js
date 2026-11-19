import { useEffect, useState } from 'react';
import { api, errorMessage } from '../services/api';

export function useResource(endpoint) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;
    const controller = new AbortController();
    setLoading(true); setError('');
    api.get(endpoint, { signal: controller.signal })
      .then(({ data }) => { if (active) setItems(data); })
      .catch(err => { if (active && err.code !== 'ERR_CANCELED') setError(errorMessage(err)); })
      .finally(() => { if (active) setLoading(false); });
    return () => { active = false; controller.abort(); };
  }, [endpoint]);

  async function save(id, payload) {
    setError('');
    try {
      if (id == null) await api.post(endpoint, payload);
      else await api.put(`${endpoint}/${id}`, payload);
      const { data } = await api.get(endpoint);
      setItems(data);
      return true;
    } catch (err) { setError(errorMessage(err)); return false; }
  }

  async function remove(id) {
    setError('');
    try {
      await api.delete(`${endpoint}/${id}`);
      setItems(previous => previous.filter(item => item.id !== id));
      return true;
    } catch (err) { setError(errorMessage(err)); return false; }
  }

  return { items, loading, error, save, remove };
}
